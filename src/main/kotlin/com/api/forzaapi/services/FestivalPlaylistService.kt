package com.api.forzaapi.services

import com.api.forzaapi.dto.request.FestivalPlaylistReq
import com.api.forzaapi.dto.responses.FestivalPlaylistResp
import com.api.forzaapi.dto.responses.PlaylistRewardVehicle
import com.api.forzaapi.entity.FestivalPlaylist
import com.api.forzaapi.repositories.FestivalPlaylistRepository
import com.api.forzaapi.repositories.GameRepository
import com.api.forzaapi.repositories.GameVehicleStatsRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FestivalPlaylistService (
    private val festivalPlaylistRepository: FestivalPlaylistRepository,
    private val gameRepository: GameRepository,
    private val gameVehicleStatsRepository: GameVehicleStatsRepository
){
    /**
     * 1. CREATE SINGLE FESTIVAL PLAYLIST
     */
    @Transactional
    fun createPlaylist(request: FestivalPlaylistReq): FestivalPlaylistResp {
        val game = gameRepository.findByIdOrNull(request.gameId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game dengan ID ${request.gameId} tidak ditemukan")

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

    /**
     * 2. BULK CREATE FESTIVAL PLAYLISTS (SKIP & CONTINUE)
     */
    @Transactional
    fun bulkCreatePlaylists(requests: List<FestivalPlaylistReq>): List<FestivalPlaylistResp> {
        val validPlaylists = mutableListOf<FestivalPlaylist>()

        for (it in requests) {
            val game = gameRepository.findByIdOrNull(it.gameId)
            if (game == null) {
                println("Skip Bulk: Game ID ${it.gameId} tidak eksis.")
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

    /**
     * 3. UPDATE FESTIVAL PLAYLIST BY ID
     */
    @Transactional
    fun updatePlaylist(id: Int, request: FestivalPlaylistReq): FestivalPlaylistResp {
        val playlist = festivalPlaylistRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist dengan ID $id tidak ditemukan")

        val game = gameRepository.findByIdOrNull(request.gameId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game tidak valid")

        val rewardEntities = gameVehicleStatsRepository.findAllById(request.rewardVehicleStatsIds).toMutableSet()

        // Timpa data lama
        playlist.game = game
        playlist.seriesNumber = request.seriesNumber
        playlist.season = request.season
        playlist.pointsRequired = request.pointsRequired
        playlist.rewardType = request.rewardType
        playlist.startDate = request.startDate
        playlist.endDate = request.endDate

        // Kosongkan hadiah lama, isi dengan set hadiah yang baru (Sifat ManyToMany Hibernate friendly)
        playlist.rewards.clear()
        playlist.rewards.addAll(rewardEntities)

        return festivalPlaylistRepository.save(playlist).toResponse()
    }

    /**
     * 4. DELETE FESTIVAL PLAYLIST BY ID
     */
    @Transactional
    fun deletePlaylist(id: Int): String {
        val playlist = festivalPlaylistRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist dengan ID $id tidak ditemukan")

        // Hapus kaitan relasi ManyToMany terlebih dahulu di memori agar tabel bridge terhapus mulus
        playlist.rewards.clear()
        festivalPlaylistRepository.delete(playlist)

        return "Success delete Festival Playlist Series ${playlist.seriesNumber} season ${playlist.season}"
    }

    // Mapper Helper dari Entity ke Response DTO
    private fun FestivalPlaylist.toResponse(): FestivalPlaylistResp {
        return FestivalPlaylistResp(
            id = this.id,
            gameTitle = this.game.title,
            seriesNumber = this.seriesNumber,
            season = this.season,
            pointsRequired = this.pointsRequired,
            rewardType = this.rewardType,
            startDate = this.startDate,
            endDate = this.endDate,
            rewards = this.rewards.map {
                PlaylistRewardVehicle(
                    gameVehicleStatsId = it.id,
                    modelName = it.vehicle.modelName,
                    performanceClass = it.performanceclass.name,
                    performanceRating = it.performancerating
                )
            }
        )
    }
}