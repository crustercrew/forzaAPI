package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Game
import com.api.forzaapi.entity.GameVehicleStats
import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GameVehicleStatsRepository: JpaRepository<GameVehicleStats,Int> {
    fun existsByVehicleAndGame(vehicle: Vehicles, game: Game):Boolean

    @Query("""
        SELECT gvs FROM GameVehicleStats gvs
        WHERE (:vehicleId IS NULL OR gvs.vehicle.id = :vehicleId)
          AND (:manufacturerId IS NULL OR gvs.vehicle.manufacturer.id = :manufacturerId)
          AND (:divisionId IS NULL OR gvs.division.id = :divisionId)
          AND (:gameId IS NULL OR gvs.game.id = :gameId)
          AND (:rarity IS NULL OR gvs.rarity = :rarity)
          AND (:driveType IS NULL OR gvs.vehicle.driveType = :driveType)
          AND (:performanceClass IS NULL OR gvs.performanceclass = :performanceClass)
    """)
    fun findWithFilters(
        @Param("vehicleId") vehicleId: Int?,
        @Param("manufacturerId") manufacturerId: Int?,
        @Param("divisionId") divisionId: Int?,
        @Param("gameId") gameId: Int?,
        @Param("rarity") rarity: Rarity?,
        @Param("driveType") driveType: String?,
        @Param("performanceClass") performanceClass: PerformanceClass?,
        pageable: Pageable
    ): Page<GameVehicleStats>
}