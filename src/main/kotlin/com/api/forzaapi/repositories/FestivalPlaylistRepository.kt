package com.api.forzaapi.repositories

import com.api.forzaapi.entity.FestivalPlaylist
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface FestivalPlaylistRepository: JpaRepository<FestivalPlaylist,Int> {
    fun findByGameIdAndSeriesNumberAndSeasonIgnoreCase(gameId: Int, seriesNumber: Int, season: String, pageable: Pageable): Page<FestivalPlaylist>
    fun findByGameIdAndSeriesNumber(gameId: Int, seriesNumber: Int, pageable: Pageable): Page<FestivalPlaylist>
    fun findByGameIdAndSeasonIgnoreCase(gameId: Int, season: String, pageable: Pageable): Page<FestivalPlaylist>
    fun findByGameId(gameId: Int, pageable: Pageable): Page<FestivalPlaylist>

    // 1. Kueri untuk Skenario Normal (Game Live)
    // Mencari playlist yang sedang berjalan HARI INI
    fun findByGameIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        gameId: Int,
        startDateCheck: LocalDate,
        endDateCheck: LocalDate,
        pageable: Pageable
    ): Page<FestivalPlaylist>

    // 2. Kueri untuk Skenario Fallback (Game Dead / EOL)
    // Mengambil 1 playlist dengan tanggal paling terakhir (Mentok ujung)
    fun findTopByGameIdOrderByEndDateDesc(gameId: Int): FestivalPlaylist?
}