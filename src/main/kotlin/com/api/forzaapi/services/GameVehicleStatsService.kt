package com.api.forzaapi.services

import com.api.forzaapi.dto.request.GameVehicleStatsReq
import com.api.forzaapi.dto.responses.*
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import com.api.forzaapi.entity.GameVehicleStats
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock
import com.api.forzaapi.repositories.DivisionRepository
import com.api.forzaapi.repositories.GameRepository
import com.api.forzaapi.repositories.GameVehicleStatsRepository
import com.api.forzaapi.repositories.VehiclesRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class GameVehicleStatsService(
    private val gameVehicleStatsRepository: GameVehicleStatsRepository,
    private val vehiclesRepository: VehiclesRepository,
    private val gameRepository: GameRepository,
    private val divisionRepository: DivisionRepository
) {

    @Transactional(readOnly = true)
    fun getStats(
        vehicleId: Int?,
        manufacturerId: Int?,
        divisionId: Int?,
        gameId: Int?,
        rarity: Rarity?,
        driveType: DriveType?,
        performanceClass: PerformanceClass?,
        pageable: Pageable
    ): PageResponse<GameVehicleStatsResp> {
        // Merakit relasi antar tabel (Join) dan filter
        val spec = Specification<GameVehicleStats> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Langsung menembak ke relasi Vehicle
            vehicleId?.let { predicates.add(cb.equal(root.get<Any>("vehicle").get<Int>("id"), it)) }
            driveType?.let { predicates.add(cb.equal(root.get<Any>("vehicle").get<DriveType>("driveType"), it)) }

            // Relasi dua tingkat: Stats -> Vehicle -> Manufacturer
            manufacturerId?.let {
                val vehicleJoin = root.join<Any, Any>("vehicle")
                predicates.add(cb.equal(vehicleJoin.get<Any>("manufacturer").get<Int>("id"), it))
            }

            // Relasi dasar lainnya
            divisionId?.let { predicates.add(cb.equal(root.get<Any>("division").get<Int>("id"), it)) }
            gameId?.let { predicates.add(cb.equal(root.get<Any>("game").get<Int>("id"), it)) }
            rarity?.let { predicates.add(cb.equal(root.get<Rarity>("rarity"), it)) }
            performanceClass?.let { predicates.add(cb.equal(root.get<PerformanceClass>("performanceclass"), it)) }

            cb.and(*predicates.toTypedArray())
        }

        // 1. Jalankan kueri dinamis
        val statsPage = gameVehicleStatsRepository.findAll(spec, pageable)

        // 2. Map ke DTO (Kode lu yang dtoList di bawahnya biarkan sama persis, tidak perlu diubah)
        val dtoList = statsPage.content.map { entity ->
            GameVehicleStatsResp(
                id = entity.id,
                game = GameResp(
                    id = entity.game.id,
                    title = entity.game.title,
                    releaseYear = entity.game.releaseYear
                ),
                division = entity.division?.let {
                    DivisionResp(id = it.id, name = it.name)
                },
                vehicle = VehiclesResp(
                    id = entity.vehicle.id,
                    modelName = entity.vehicle.modelName,
                    productionyear = entity.vehicle.productionyear,
                    manufacturer = ManufacturerResp(
                        id = entity.vehicle.manufacturer.id,
                        name = entity.vehicle.manufacturer.name,
                        country = entity.vehicle.manufacturer.country
                    ),
                    enginespec = entity.vehicle.enginespec,
                    horsepower = entity.vehicle.horsepower,
                    torque = entity.vehicle.torque,
                    driveType = entity.vehicle.driveType.name,
                    drivetrain = entity.vehicle.drivetrain.name,
                    transmission = entity.vehicle.transmission,
                    weightkg = entity.vehicle.weightkg,
                    weightdistribution = entity.vehicle.weightdistribution
                ),
                rarity = entity.rarity,
                unlockType = entity.unlocktype,
                performanceClass = entity.performanceclass,
                performanceRating = entity.performancerating,
                stats = VehicleMetricsResp(
                    speed = entity.statSpeed,
                    handling = entity.statHandling,
                    acceleration = entity.statAcceleration,
                    launch = entity.statLaunch,
                    braking = entity.statBraking,
                    offroad = entity.statOffroad
                ),
                acquisition = VehicleAcquisitionResp(
                    autoshowCost = entity.autoshowCost,
                    forzathonShopCost = entity.forzathonShopCost,
                    isBackstageAvailable = entity.isBackstageAvailable,
                    dlcRequired = entity.dlcRequired
                )
            )
        }

        return PageResponse(
            page = statsPage.number, size = statsPage.size, totalElements = statsPage.totalElements,
            totalPages = statsPage.totalPages, data = dtoList
        )
    }

    @Transactional(readOnly = true)
    fun getStatsById(id: Int): GameVehicleStatsResp =
        gameVehicleStatsRepository.findByIdOrNull(id)
            ?.toResponse()
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Record statistik dengan ID $id tidak ditemukan"
            )

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
            game = GameResp(
                id = this.game.id,
                title = this.game.title,
                releaseYear = this.game.releaseYear
            ),
            division = DivisionResp(
                id = this.division?.id ?: 0,
                name = this.division?.name ?: "Unknown"
            ),
            vehicle = VehiclesResp(
                id = this.vehicle.id,
                modelName = this.vehicle.modelName,
                productionyear = this.vehicle.productionyear,
                manufacturer = ManufacturerResp(
                    id = this.vehicle.manufacturer.id,
                    name = this.vehicle.manufacturer.name,
                    country = this.vehicle.manufacturer.country
                ),
                enginespec = this.vehicle.enginespec,
                horsepower = this.vehicle.horsepower,
                torque = this.vehicle.torque,
                driveType = this.vehicle.driveType.name,
                drivetrain = this.vehicle.drivetrain.name,
                transmission = this.vehicle.transmission,
                weightkg = this.vehicle.weightkg,
                weightdistribution = this.vehicle.weightdistribution,
            ),
            rarity = Rarity.valueOf(this.rarity.name),
            unlockType = UniqueUnlock.valueOf(this.unlocktype.name),
            performanceClass = this.performanceclass,
            performanceRating = this.performancerating,
            stats = VehicleMetricsResp(
                speed = this.statSpeed,
                handling = this.statHandling,
                acceleration = this.statAcceleration,
                launch = this.statLaunch,
                braking = this.statBraking,
                offroad = this.statOffroad
            ),
            acquisition = VehicleAcquisitionResp(
                autoshowCost = this.autoshowCost,
                forzathonShopCost = this.forzathonShopCost,
                dlcRequired = this.dlcRequired,
                isBackstageAvailable = this.isBackstageAvailable
            ),
        )
    }
}