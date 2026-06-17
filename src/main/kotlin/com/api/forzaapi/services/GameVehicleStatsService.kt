package com.api.forzaapi.services

import com.api.forzaapi.dto.request.GameVehicleStatsReq
import com.api.forzaapi.dto.responses.GameVehicleStatsResp
import com.api.forzaapi.dto.responses.PerformanceProfile
import com.api.forzaapi.entity.GameVehicleStats
import com.api.forzaapi.repositories.DivisionRepository
import com.api.forzaapi.repositories.GameRepository
import com.api.forzaapi.repositories.GameVehicleStatsRepository
import com.api.forzaapi.repositories.VehiclesRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class GameVehicleStatsService(
    private val gameVehicleStatsRepository: GameVehicleStatsRepository,
    private val vehiclesRepository: VehiclesRepository,
    private val gameRepository: GameRepository,
    private val divisionRepository: DivisionRepository
) {
    /**
     * 1. Create game VehicleStats
     */
    @Transactional
    fun createStats(request: GameVehicleStatsReq): GameVehicleStatsResp {
        // 1. Validasi Keberadaan Relasi Parent
        val vehicle = vehiclesRepository.findByIdOrNull(request.vehicleId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle dengan ID ${request.vehicleId} tidak ditemukan")

        val game = gameRepository.findByIdOrNull(request.gameId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game dengan ID ${request.gameId} tidak ditemukan")

        // Cari division jika id dikirim (karena di entity field ini nullable)
        val division = request.divisionId?.let {
            divisionRepository.findByIdOrNull(it)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Division dengan ID $it tidak ditemukan")
        }

        // 2. Validasi Duplikasi (1 Mobil hanya boleh punya 1 Record Stat per Game)
        val isExist = gameVehicleStatsRepository.existsByVehicleAndGame(vehicle, game)
        if (isExist) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Statistik untuk mobil '${vehicle.modelName}' di game '${game.title}' sudah terdaftar!"
            )
        }

        // 3. Mapping DTO Request ke Entity Baru kamu (ID di-set 0 agar auto-increment)
        val statsEntity = GameVehicleStats(
            id = 0,
            vehicle = vehicle,
            game = game,
            division = division,
            rarity = request.rarity,
            unlocktype = request.unlocktype,
            performanceclass = request.performanceclass,
            performancerating = request.performancerating,
            statSpeed = request.statSpeed,
            statHandling = request.statHandling,
            statAcceleration = request.statAcceleration,
            statLaunch = request.statLaunch,
            statBraking = request.statBraking,
            statOffroad = request.statOffroad,
            autoshowCost = request.autoshowCost,
            dlcRequired = request.dlcRequired,
            forzathonShopCost = request.forzathonShopCost,
            isBackstageAvailable = request.isBackstageAvailable
        )

        // 4. Save dan kembalikan response DTO
        val savedEntity = gameVehicleStatsRepository.save(statsEntity)
        return savedEntity.toResponse()
    }

    /**
     * 2. Bulk Create game VehicleStats
     */
    @Transactional
    fun bulkCreateStats(requests: List<GameVehicleStatsReq>): List<GameVehicleStatsResp> {
        val validStatsList = mutableListOf<GameVehicleStats>()

        for (it in requests) {
            // 1. Safe Check Parent Relasi (Jika salah satu null, skip ke perulangan berikutnya)
            val vehicle = vehiclesRepository.findByIdOrNull(it.vehicleId) ?: continue
            val game = gameRepository.findByIdOrNull(it.gameId) ?: continue

            val division = it.divisionId?.let { divId ->
                divisionRepository.findByIdOrNull(divId)
            }

            // 2. Safe Check Duplikasi di Database
            val isExistInDb = gameVehicleStatsRepository.existsByVehicleAndGame(vehicle, game)
            if (isExistInDb) {
                println("Skip Bulk: Stat untuk mobil '${vehicle.modelName}' di game '${game.title}' sudah ada di database.")
                continue
            }

            // 3. Safe Check Duplikasi internal di dalam list request yang sedang dikirim
            val isDuplicateInList = validStatsList.any { added ->
                added.vehicle.id == vehicle.id && added.game.id == game.id
            }
            if (isDuplicateInList) continue

            // Jika lolos semua validasi, bungkus jadi Entity dan masukkan ke antrean
            val statsEntity = GameVehicleStats(
                id = 0,
                vehicle = vehicle,
                game = game,
                division = division,
                rarity = it.rarity,
                unlocktype = it.unlocktype,
                performanceclass = it.performanceclass,
                performancerating = it.performancerating,
                statSpeed = it.statSpeed,
                statHandling = it.statHandling,
                statAcceleration = it.statAcceleration,
                statLaunch = it.statLaunch,
                statBraking = it.statBraking,
                statOffroad = it.statOffroad,
                autoshowCost = it.autoshowCost,
                dlcRequired = it.dlcRequired,
                forzathonShopCost = it.forzathonShopCost,
                isBackstageAvailable = it.isBackstageAvailable
            )
            validStatsList.add(statsEntity)
        }

        // Simpan massal sekaligus dengan performa Batching yang optimal
        val savedEntities = gameVehicleStatsRepository.saveAll(validStatsList)
        return savedEntities.map { it.toResponse() }
    }

    /**
     * 3. update game VehicleStats
     */
    @Transactional
    fun updateStats(id: Int, request: GameVehicleStatsReq): GameVehicleStatsResp {
        // Cek record statistik utama yang mau di-update
        val currentStats = gameVehicleStatsRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Record statistik dengan ID $id tidak ditemukan")

        // Cari parent relasi baru jika user ingin mengganti relasi mobil/game/division
        val vehicle = vehiclesRepository.findByIdOrNull(request.vehicleId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle tidak valid")

        val game = gameRepository.findByIdOrNull(request.gameId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game tidak valid")

        val division = request.divisionId?.let {
            divisionRepository.findByIdOrNull(it) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Division tidak valid")
        }

        // Proteksi: Jika mengganti mobil/game, pastikan kombinasi barunya tidak menabrak record lain yang sudah ada
        if (currentStats.vehicle.id != vehicle.id || currentStats.game.id != game.id) {
            val isViolatingUnique = gameVehicleStatsRepository.existsByVehicleAndGame(vehicle, game)
            if (isViolatingUnique) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal Update: Kombinasi mobil dan game tersebut sudah ada di record lain!")
            }
        }

        // Timpa data lama entity dengan kiriman request DTO baru
        currentStats.vehicle = vehicle
        currentStats.game = game
        currentStats.division = division
        currentStats.rarity = request.rarity
        currentStats.unlocktype = request.unlocktype
        currentStats.performanceclass = request.performanceclass
        currentStats.performancerating = request.performancerating
        currentStats.statSpeed = request.statSpeed
        currentStats.statHandling = request.statHandling
        currentStats.statAcceleration = request.statAcceleration
        currentStats.statLaunch = request.statLaunch
        currentStats.statBraking = request.statBraking
        currentStats.statOffroad = request.statOffroad
        currentStats.autoshowCost = request.autoshowCost
        currentStats.dlcRequired = request.dlcRequired
        currentStats.forzathonShopCost = request.forzathonShopCost
        currentStats.isBackstageAvailable = request.isBackstageAvailable

        return gameVehicleStatsRepository.save(currentStats).toResponse()
    }

    /**
     * 4. delete game VehicleStats
     */
    @Transactional
    fun deleteStats(id: Int): String {
        val currentStats = gameVehicleStatsRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Record statistik dengan ID $id tidak ditemukan")

        gameVehicleStatsRepository.delete(currentStats)
        return "Success delete statistik ID $id untuk mobil '${currentStats.vehicle.modelName}' di game '${currentStats.game.title}'"
    }

    // Extension function mapping dari Entity ke Response DTO
    private fun GameVehicleStats.toResponse(): GameVehicleStatsResp {
        return GameVehicleStatsResp(
            id = this.id,
            vehicleModelName = this.vehicle.modelName,
            gameTitle = this.game.title,
            divisionName = this.division?.name, // Ambil nama dari objek relasi Division
            rarity = this.rarity.name,
            unlockType = this.unlocktype.name,
            performanceProfile = PerformanceProfile(
                className = this.performanceclass.name,
                rating = this.performancerating
            ),
            metrics = mapOf(
                "speed" to this.statSpeed,
                "handling" to this.statHandling,
                "acceleration" to this.statAcceleration,
                "launch" to this.statLaunch,
                "braking" to this.statBraking,
                "offroad" to this.statOffroad
            ),
            dlcRequired = this.dlcRequired,
            forzathonShopCost = this.forzathonShopCost,
            isBackstageAvailable = this.isBackstageAvailable
        )
    }
}