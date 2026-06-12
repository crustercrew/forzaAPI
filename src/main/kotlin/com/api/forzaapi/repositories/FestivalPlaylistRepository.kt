package com.api.forzaapi.repositories

import com.api.forzaapi.entity.FestivalPlaylist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FestivalPlaylistRepository: JpaRepository<FestivalPlaylist,String> {
    fun findBySeriesName(seriesName: String): List<FestivalPlaylist>
    fun findBySeasonName(seasonName: String): List<FestivalPlaylist>
    fun findByGameName(gameName: String): List<FestivalPlaylist>
    fun findBySeriesNameAndSeasonName(seriesName: String, seasonName: String): List<FestivalPlaylist>
    fun findBySeriesNameAndGameName(seriesName: String, gameName: String): List<FestivalPlaylist>
}