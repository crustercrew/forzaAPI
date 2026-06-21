package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VehiclesRepository: JpaRepository<Vehicles,Int>, JpaSpecificationExecutor<Vehicles> {
    fun findByModelNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Vehicles>
    fun existsByModelNameAndProductionyear(modeName: String, productionYear: Int): Boolean
}