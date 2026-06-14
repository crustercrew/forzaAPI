package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Vehicles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehiclesRepository: JpaRepository<Vehicles,Int> {
    fun findByModelName(modelName: String): Vehicles
    fun existsByModelNameAndProductionyear(modeName:String,productionYear:Int): Boolean
}