package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Game
import com.api.forzaapi.entity.GameVehicleStats
import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GameVehicleStatsRepository: JpaRepository<GameVehicleStats,Int>, JpaSpecificationExecutor<GameVehicleStats> {
    fun existsByVehicleAndGame(vehicle: Vehicles, game: Game):Boolean
}