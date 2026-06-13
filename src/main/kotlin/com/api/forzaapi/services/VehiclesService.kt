package com.api.forzaapi.services

import com.api.forzaapi.dto.responses.ManufacturerResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.repositories.VehiclesRepository
import com.api.forzaapi.utils.toPageResponse
import org.springframework.stereotype.Service
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.data.repository.findByIdOrNull

@Service
class VehiclesService(
    private val vehiclesRepository: VehiclesRepository
) {
    fun getAllVehicles(pageable: Pageable): PageResponse<VehiclesResp> {
        return vehiclesRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    fun getVehicleById(id: String): VehiclesResp? {
        val vehicle = vehiclesRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle with id $id not found")
        return vehicle.toResponse()
    }

    private fun Vehicles.toResponse(): VehiclesResp {
        return VehiclesResp(
            id = this.id,
            modelName = this.modelName,
            productionyear = this.productionyear,
            manufacturer = ManufacturerResp(

                name = this.manufacturer.name,
                country = this.manufacturer.country,
                id = this.manufacturer.id
            ),
            enginespec = this.enginespec,
            horsepower = this.horsepower,
            torque = this.torque,
            driveType = this.driveType.name,
            drivetrain = this.drivetrain.name,
            transmission = this.transmission,
            weightkg = this.weightkg,
            weightdistribution = this.weightdistribution
        )
    }
}