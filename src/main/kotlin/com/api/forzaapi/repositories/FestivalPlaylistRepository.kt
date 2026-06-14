package com.api.forzaapi.repositories

import com.api.forzaapi.entity.FestivalPlaylist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FestivalPlaylistRepository: JpaRepository<FestivalPlaylist,Int> {
    fun findBySeriesNumber(seriesNumber: Int): List<FestivalPlaylist>
    fun findBySeason(season: String): List<FestivalPlaylist>
    fun findByGame_Title(gameTitle: String): List<FestivalPlaylist>
    fun findBySeriesNumberAndSeason(
        seriesNumber: Int,
        season: String
    ): List<FestivalPlaylist>
    fun findBySeriesNumberAndGame_Title(
        seriesNumber: Int,
        gameTitle: String
    ): List<FestivalPlaylist>
}