package com.api.forzaapi.repositories

import com.api.forzaapi.entity.FestivalPlaylist
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FestivalPlaylistRepository: JpaRepository<FestivalPlaylist,Int> {

    // Spring Data otomatis tahu cara membuat WHERE clause dinamis yang aman dari NULL tanpa crash
    fun findByGameIdAndSeriesNumberAndSeasonIgnoreCase(gameId: Int, seriesNumber: Int, season: String, pageable: Pageable): Page<FestivalPlaylist>
    fun findByGameIdAndSeriesNumber(gameId: Int, seriesNumber: Int, pageable: Pageable): Page<FestivalPlaylist>
    fun findByGameIdAndSeasonIgnoreCase(gameId: Int, season: String, pageable: Pageable): Page<FestivalPlaylist>
    fun findByGameId(gameId: Int, pageable: Pageable): Page<FestivalPlaylist>
}