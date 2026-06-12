package com.api.forzaapi.repositories

import com.api.forzaapi.entity.GameVehicleStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameVehicleStatsRepository: JpaRepository<GameVehicleStats,String> {
    fun findByVehicleName(vehicleName: String): List<GameVehicleStats>
    fun findByGameName(gameName: String): List<GameVehicleStats>
    fun findByVehicleNameAndGameName(vehicleName: String, gameName: String): GameVehicleStats?
}