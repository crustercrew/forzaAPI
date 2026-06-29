package com.api.forzaapi.services

import com.api.forzaapi.dto.request.VehiclesReq
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehicleImagesResp
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import com.api.forzaapi.entity.Vehicles
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import com.api.forzaapi.repositories.ManufacturersRepository
import com.api.forzaapi.repositories.VehiclesRepository
import com.api.forzaapi.utils.toPageResponse
import jakarta.persistence.criteria.Predicate
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class VehiclesService(
    private val vehiclesRepository: VehiclesRepository,
    private val manufacturersRepository: ManufacturersRepository
) {

//    @Cacheable(
//        value = ["vehicles"],
//        key = "{#manufacturerId, #startYear, #endYear, #driveType, #drivetrain, #pageable.pageNumber, #pageable.pageSize}"
//    )
    @Transactional(readOnly = true)
    fun getVehiclesWithFilters(
        manufacturerId:Int?,
        startYear:Int?,
        endYear:Int?,
        driveType: DriveType?,
        drivetrain: Drivetrain?,
        pageable: Pageable
    ): PageResponse<VehiclesResp> {
        val spec = Specification<Vehicles>{
            root, query, builder ->
            val predicates = mutableListOf<Predicate>()

            manufacturerId?.let { predicates.add(builder.equal(root.get<Any>("manufacturer").get<Int>("id"), it)) }
            startYear?.let { predicates.add(builder.greaterThanOrEqualTo(root.get("productionyear"), it)) }
            endYear?.let { predicates.add(builder.lessThanOrEqualTo(root.get("productionyear"), it)) }
            driveType?.let { predicates.add(builder.equal(root.get<DriveType>("driveType"), it)) }
            drivetrain?.let { predicates.add(builder.equal(root.get<Drivetrain>("drivetrain"), it)) }

            builder.and(*predicates.toTypedArray())
        }

        return vehiclesRepository.findAll(spec, pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    @Transactional(readOnly = true)
    fun getVehicleById(id: Int): VehiclesResp? {
        val vehicle = vehiclesRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle with id $id not found")
        return vehicle.toResponse()
    }

    @Transactional(readOnly = true)
    fun searchVehiclesByModelName(modelName: String, pageable: Pageable): PageResponse<VehiclesResp>?{
        return vehiclesRepository.findByModelNameContainingIgnoreCase(modelName,pageable).map { it.toResponse() }.toPageResponse();
    }

    @Transactional
    fun createVehicle(request: VehiclesReq): VehiclesResp {

        val manufacturer = manufacturersRepository.findByNameIgnoreCase(request.manufacturerName)
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
            weightlbs = request.weightlbs,
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
            val manufacturer = manufacturersRepository.findByNameIgnoreCase(it.manufacturerName)
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
                weightlbs = it.weightlbs,
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

        val manufacturer = manufacturersRepository.findByNameIgnoreCase(request.manufacturerName)
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
        vehicle.weightlbs = request.weightlbs
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
            weightlbs = this.weightlbs,
            weightdistribution = this.weightdistribution,
            description = this.description,
            images = this.images.map { it.imageUrl }
        )
    }
}