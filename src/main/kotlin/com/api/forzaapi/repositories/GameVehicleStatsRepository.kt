package com.api.forzaapi.repositories

import com.api.forzaapi.entity.GameVehicleStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameVehicleStatsRepository: JpaRepository<GameVehicleStats,String> {
    fun findByVehicle_ModelName(vehicleModelName: String): List<GameVehicleStats>
    fun findByGame_Title(gameName: String): List<GameVehicleStats>
}