package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Game
import com.api.forzaapi.entity.GameVehicleStats
import com.api.forzaapi.entity.Vehicles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameVehicleStatsRepository: JpaRepository<GameVehicleStats,Int> {
    fun findByVehicle_ModelName(vehicleModelName: String): List<GameVehicleStats>
    fun findByGame_Title(gameName: String): List<GameVehicleStats>
    fun existsByVehicleAndGame(vehicle: Vehicles, game: Game):Boolean
}