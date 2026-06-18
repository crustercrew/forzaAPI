package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VehiclesRepository: JpaRepository<Vehicles,Int> {
    fun findByModelNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Vehicles>
    @Query("""
        SELECT v FROM Vehicles v 
        WHERE (:manufacturerId IS NULL OR v.manufacturer.id = :manufacturerId)
          AND (:startYear IS NULL OR v.productionyear >= :startYear)
          AND (:endYear IS NULL OR v.productionyear <= :endYear)
          AND (:driveType IS NULL OR v.driveType = :driveType)
          AND (:drivetrain IS NULL OR v.drivetrain = :drivetrain)
    """)
    fun findVehiclesWithFilters(
        @Param("manufacturerId") manufacturerId: Int?,
        @Param("startYear") startYear: Int?,
        @Param("endYear") endYear: Int?,
        @Param("driveType") driveType: DriveType?,
        @Param("drivetrain") drivetrain: Drivetrain?,
        pageable: Pageable
    ): Page<Vehicles>
    fun existsByModelNameAndProductionyear(modeName:String,productionYear:Int): Boolean
}