package com.api.forzaapi.services

import com.api.forzaapi.dto.request.VehiclesReq
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.entity.Manufacturers
import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import com.api.forzaapi.repositories.ManufacturersRepository
import com.api.forzaapi.repositories.VehiclesRepository
import com.api.forzaapi.utils.toPageResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.data.repository.findByIdOrNull

@Service
class VehiclesService(
    private val vehiclesRepository: VehiclesRepository,
    private val manufacturersRepository: ManufacturersRepository
) {
    fun getAllVehicles(pageable: Pageable): PageResponse<VehiclesResp> {
        return vehiclesRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    fun getVehicleById(id: Int): VehiclesResp? {
        val vehicle = vehiclesRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle with id $id not found")
        return vehicle.toResponse()
    }

    fun getVehicleByModelName(modelName: String):VehiclesResp?{
        val vehicle = vehiclesRepository.findByModelName(modelName)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND,"vehicle with model name $modelName not found")
        return vehicle.toResponse()
    }

    @Transactional
    fun createVehicle(request: VehiclesReq): VehiclesResp {

        val manufacturer = manufacturersRepository.findByNameContainingIgnoreCase(request.manufacturerName)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer Not Found")

        val isVehicleExist = vehiclesRepository.existsByModelNameAndProductionyear(request.modelName, request.productionyear)
        if (isVehicleExist) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobil ${request.modelName} with Year ${request.productionyear} is Already Exist!")
        }
        val vehicle = Vehicles(
            id = 0,
            modelName = request.modelName,
            productionyear = request.productionyear,
            manufacturer = manufacturer,
            enginespec = request.enginespec,
            horsepower = request.horsepower,
            torque = request.torque,
            driveType = request.driveType,
            drivetrain = request.drivetrain,
            transmission = request.transmission,
            weightkg = request.weightkg,
            weightdistribution = request.weightdistribution,
            description = request.description
        )
        return vehiclesRepository.save(vehicle).toResponse()
    }

    @Transactional
    fun bulkCreate(requests: List<VehiclesReq>): List<VehiclesResp> {
        val validVehicles = mutableListOf<Vehicles>()

        for (it in requests) {
            // 1. Safe Check Manufacturer (Kalau null, kita skip, gak pake throw)
            val manufacturer = manufacturersRepository.findByNameContainingIgnoreCase(it.manufacturerName)
            if (manufacturer == null) {
                println("Skip: Manufacturer ${it.manufacturerName} tidak ditemukan.")
                continue
            }

            // 2. Safe Check Duplicate (Kalau ada di DB, kita skip)
            val isVehicleExist = vehiclesRepository.existsByModelNameAndProductionyear(it.modelName, it.productionyear)
            if (isVehicleExist) {
                println("Skip: Mobil ${it.modelName} (${it.productionyear}) sudah ada di DB.")
                continue
            }

            // 3. Tambahan: Proteksi duplikasi internal di dalam list request yang dikirim
            val isDuplicateInList = validVehicles.any { vehicle ->
                vehicle.modelName == it.modelName && vehicle.productionyear == it.productionyear
            }
            if (isDuplicateInList) {
                continue
            }

            // Jika lolos semua validasi, masukkan ke antrean simpan
            val vehicle = Vehicles(
                id = 0,
                modelName = it.modelName,
                productionyear = it.productionyear,
                manufacturer = manufacturer,
                enginespec = it.enginespec,
                horsepower = it.horsepower,
                torque = it.torque,
                driveType = it.driveType,
                drivetrain = it.drivetrain,
                transmission = it.transmission,
                weightkg = it.weightkg,
                weightdistribution = it.weightdistribution,
                description = it.description
            )
            validVehicles.add(vehicle)
        }

        // 4. Gunakan saveAll untuk insert massal sekaligus ke DB (Jauh lebih cepat daripada save satu-satu di dalam map)
        val savedVehicles = vehiclesRepository.saveAll(validVehicles)

        return savedVehicles.map { it.toResponse() }
    }

    @Transactional
    fun updateVehicle(id: Int, request: VehiclesReq): VehiclesResp{
        val vehicle = vehiclesRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle with id $id not found")

        val manufacturer = manufacturersRepository.findByNameContainingIgnoreCase(request.manufacturerName)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer Not Found")

        vehicle.modelName = request.modelName
        vehicle.productionyear = request.productionyear
        vehicle.manufacturer = manufacturer
        vehicle.enginespec = request.enginespec
        vehicle.horsepower = request.horsepower
        vehicle.torque = request.torque
        vehicle.driveType = request.driveType
        vehicle.drivetrain = request.drivetrain
        vehicle.transmission = request.transmission
        vehicle.weightkg = request.weightkg
        vehicle.weightdistribution = request.weightdistribution
        vehicle.description = request.description

        return vehiclesRepository.save(vehicle).toResponse()
    }

    @Transactional
    fun deleteVehicle(id: Int):String {
        val vehicle = vehiclesRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle with id $id not found")
        vehiclesRepository.delete(vehicle)
        return "Success delete model name ${vehicle.modelName}"
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