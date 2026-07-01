package com.api.forzaapi.services

import com.api.forzaapi.dto.request.FestivalPlaylistReq
import com.api.forzaapi.dto.responses.DivisionResp
import com.api.forzaapi.dto.responses.FestivalPlaylistResp
import com.api.forzaapi.dto.responses.GameResp
import com.api.forzaapi.dto.responses.GameVehicleStatsResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehicleAcquisitionResp
import com.api.forzaapi.dto.responses.VehicleImagesResp
import com.api.forzaapi.dto.responses.VehicleMetricsResp
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.dto.responses.ManufacturerResp
import com.api.forzaapi.entity.FestivalPlaylist
import com.api.forzaapi.repositories.FestivalPlaylistRepository
import com.api.forzaapi.repositories.GameRepository
import com.api.forzaapi.repositories.GameVehicleStatsRepository
import com.api.forzaapi.utils.errorhandler.ResourceNotFoundException
import com.api.forzaapi.utils.toPageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class FestivalPlaylistService (
    private val festivalPlaylistRepository: FestivalPlaylistRepository,
    private val gameRepository: GameRepository,
    private val gameVehicleStatsRepository: GameVehicleStatsRepository
){
    @Transactional(readOnly = true)
    fun getAllPlaylistsWithFilters(
        gameId: Int?,
        seriesNumber: Int?,
        season: String?,
        pageable: Pageable
    ): PageResponse<FestivalPlaylistResp> {
        val statsPage = when {
            gameId != null && seriesNumber != null && !season.isNullOrBlank() -> {
                festivalPlaylistRepository.findByGameIdAndSeriesNumberAndSeasonIgnoreCase(gameId, seriesNumber, season, pageable)
            }
            gameId != null && seriesNumber != null -> {
                festivalPlaylistRepository.findByGameIdAndSeriesNumber(gameId, seriesNumber, pageable)
            }
            gameId != null && !season.isNullOrBlank() -> {
                festivalPlaylistRepository.findByGameIdAndSeasonIgnoreCase(gameId, season, pageable)
            }
            gameId != null -> {
                festivalPlaylistRepository.findByGameId(gameId, pageable)
            }
            else -> {
                festivalPlaylistRepository.findAll(pageable) // Jika semua kosong, tampilkan semua playlist
            }
        }
        return PageResponse(
            data = statsPage.content.map { it.toResponse() },
            page = statsPage.number,
            size = statsPage.size,
            totalElements = statsPage.totalElements,
            totalPages = statsPage.totalPages
        )
    }

    @Transactional(readOnly = true)
    fun getPlaylistById(id: Int): FestivalPlaylistResp? {
        val playlist = festivalPlaylistRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Playlist with ID $id Not Found")
        return playlist.toResponse()
    }

    @Transactional(readOnly = true)
    fun getCurrentPlaylist(gameId: Int,pageable: Pageable): PageResponse<FestivalPlaylistResp>{
        val today = LocalDate.now()

        val activePlaylists = festivalPlaylistRepository.
        findByGameIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(gameId,today,today,pageable)

        if(!activePlaylists.isEmpty){
            return activePlaylists.map { it.toResponse() }.toPageResponse()
        }

        val lastKnownPlaylist = festivalPlaylistRepository.findTopByGameIdOrderByEndDateDesc(gameId)
            ?: throw ResourceNotFoundException( "There is no playlist data at all for this game")

        val finalSeriesPlaylists = festivalPlaylistRepository
            .findByGameIdAndSeriesNumber(gameId, lastKnownPlaylist.seriesNumber,pageable)

        return finalSeriesPlaylists.map { it.toResponse() }.toPageResponse()
    }

    @Transactional
    fun createPlaylist(request: FestivalPlaylistReq): FestivalPlaylistResp {
        val game = gameRepository.findByIdOrNull(request.gameId)
            ?: throw ResourceNotFoundException("Game with ID ${request.gameId} Not Found")

        // Cari semua entitas mobil hadiah berdasarkan ID yang dikirim
        val rewardEntities = gameVehicleStatsRepository.findAllById(request.rewardVehicleStatsIds).toMutableSet()

        val playlist = FestivalPlaylist(
            id = 0,
            game = game,
            seriesNumber = request.seriesNumber,
            season = request.season,
            pointsRequired = request.pointsRequired,
            rewardType = request.rewardType,
            startDate = request.startDate,
            endDate = request.endDate,
            rewards = rewardEntities
        )

        return festivalPlaylistRepository.save(playlist).toResponse()
    }

    @Transactional
    fun bulkCreatePlaylists(requests: List<FestivalPlaylistReq>): List<FestivalPlaylistResp> {
        val validPlaylists = mutableListOf<FestivalPlaylist>()

        for (it in requests) {
            val game = gameRepository.findByIdOrNull(it.gameId)
            if (game == null) {
                println("Skip Bulk: Game ID ${it.gameId} not Exist.")
                continue
            }

            val rewardEntities = gameVehicleStatsRepository.findAllById(it.rewardVehicleStatsIds).toMutableSet()

            val playlist = FestivalPlaylist(
                id = 0,
                game = game,
                seriesNumber = it.seriesNumber,
                season = it.season,
                pointsRequired = it.pointsRequired,
                rewardType = it.rewardType,
                startDate = it.startDate,
                endDate = it.endDate,
                rewards = rewardEntities
            )
            validPlaylists.add(playlist)
        }

        val savedPlaylists = festivalPlaylistRepository.saveAll(validPlaylists)
        return savedPlaylists.map { it.toResponse() }
    }

    @Transactional
    fun updatePlaylist(id: Int, request: FestivalPlaylistReq): FestivalPlaylistResp {
        val playlist = festivalPlaylistRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        val game = gameRepository.findByIdOrNull(request.gameId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        val rewardEntities = gameVehicleStatsRepository.findAllById(request.rewardVehicleStatsIds).toMutableSet()

        playlist.game = game
        playlist.seriesNumber = request.seriesNumber
        playlist.season = request.season
        playlist.pointsRequired = request.pointsRequired
        playlist.rewardType = request.rewardType
        playlist.startDate = request.startDate
        playlist.endDate = request.endDate

        playlist.rewards.clear()
        playlist.rewards.addAll(rewardEntities)

        return festivalPlaylistRepository.save(playlist).toResponse()
    }

    @Transactional
    fun deletePlaylist(id: Int): String {
        val playlist = festivalPlaylistRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Playlist with ID $id Not Found")
        playlist.rewards.clear()
        festivalPlaylistRepository.delete(playlist)

        return "Success delete Festival Playlist Series ${playlist.seriesNumber} season ${playlist.season}"
    }

    // Mapper Helper dari Entity ke Response DTO
    private fun FestivalPlaylist.toResponse(): FestivalPlaylistResp {
        return FestivalPlaylistResp(
            id = this.id,
            game = GameResp(
                id = this.game.id,
                title = this.game.title,
                releaseYear = this.game.releaseYear
            ),
            seriesNumber = this.seriesNumber,
            season = this.season,
            pointsRequired = this.pointsRequired,
            rewardType = this.rewardType,
            startDate = this.startDate,
            endDate = this.endDate,
            rewards = this.rewards.map {
                GameVehicleStatsResp(
                    id = it.id,
                    game = GameResp(
                        id = it.game.id,
                        title = it.game.title,
                        releaseYear = it.game.releaseYear
                    ),
                    division = it.division?.let { div ->
                        DivisionResp(
                            id = div.id,
                            name = div.name
                        )
                    }, // Lebih aman pakai safety-call ?.let jika divisi mobil-nya nullable
                    vehicle = VehiclesResp(
                        id = it.vehicle.id,
                        modelName = it.vehicle.modelName,
                        productionyear = it.vehicle.productionyear,
                        manufacturer = ManufacturerResp(
                            id = it.vehicle.manufacturer.id,
                            name = it.vehicle.manufacturer.name,
                            country = it.vehicle.manufacturer.country
                        ),
                        enginespec = it.vehicle.enginespec,
                        horsepower = it.vehicle.horsepower,
                        torque = it.vehicle.torque,
                        driveType = it.vehicle.driveType.name,
                        drivetrain = it.vehicle.drivetrain.name,
                        transmission = it.vehicle.transmission,
                        weightlbs = it.vehicle.weightlbs,
                        weightdistribution = it.vehicle.weightdistribution,
                        description = it.vehicle.description,
                        images = it.vehicle.images.map { images ->
                            VehicleImagesResp(
                                id = images.id,
                                gameseries = images.gameimageseries,
                                carimage = images.imageUrl
                            )
                        }
                    ),
                    rarity = it.rarity,
                    unlockType = it.unlocktype,
                    performanceClass = it.performanceclass,
                    performanceRating = it.performancerating,
                    stats = VehicleMetricsResp(
                        speed = it.statSpeed,
                        handling = it.statHandling,
                        acceleration = it.statAcceleration,
                        launch = it.statLaunch,
                        braking = it.statBraking,
                        offroad = it.statOffroad
                    ),
                    acquisition = VehicleAcquisitionResp(
                        autoshowCost = it.autoshowCost,
                        isBackstageAvailable = it.isBackstageAvailable,
                        dlcRequired = it.dlcRequired
                    )
                )
            }
        )
    }
}