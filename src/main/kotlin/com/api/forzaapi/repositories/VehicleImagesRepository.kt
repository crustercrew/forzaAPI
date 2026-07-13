package com.api.forzaapi.repositories

import com.api.forzaapi.entity.VehicleImages
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface VehicleImagesRepository: JpaRepository<VehicleImages,Int> {
    fun findByVehicleId(vehicleId: Int): List<VehicleImages>
}