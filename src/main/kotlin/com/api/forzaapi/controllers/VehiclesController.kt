package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.VehicleImagesReq
import com.api.forzaapi.dto.request.VehiclesReq
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehicleImagesResp
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import com.api.forzaapi.services.VehicleImagesService
import com.api.forzaapi.services.VehiclesService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.websocket.server.PathParam
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "Vehicles", description = "Endpoints to access Forza Horizon master data and car specifications")
@RestController
@RequestMapping("/vehicles")
class VehiclesController(
    private val vehiclesService: VehiclesService,
    private val vehicleImageService: VehicleImagesService
) {
    /**
     * 1. GET ALL VEHICLES & FILTERS (PAGINATED)
     * Mengakomodasi pencarian berdasarkan Pabrikan, Rentang Tahun, DriveType, dan Drivetrain.
     * * Contoh Postman / Client Android:
     * - Semua mobil: GET /api/v1/vehicles
     * - Cari Nissan: GET /api/v1/vehicles?manufacturerId=12
     * - Mobil AWD saja: GET /api/v1/vehicles?drivetrain=AWD
     * - Mobil Tahun 2000-2012: GET /api/v1/vehicles?startYear=2000&endYear=2012
     */
    @Operation(
        summary = "GET ALL VEHICLES & FILTERS (PAGINATED)",
        description = "Accommodates searches by Manufacturer, Year Range, Drive Type, and Drivetrain."
    )
    @GetMapping
    fun getVehicles(
        @RequestParam(value = "manufacturerId", required = false) manufacturerId: Int?,
        @RequestParam(value = "startYear", required = false) startYear: Int?,
        @RequestParam(value = "endYear", required = false) endYear: Int?,
        @RequestParam(value = "driveType", required = false) driveType: DriveType?,
        @RequestParam(value = "drivetrain", required = false) drivetrain: Drivetrain?,
        @ParameterObject
        @PageableDefault(page = 0, size = 20, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<PageResponse<VehiclesResp>> {

        // Panggil service yang memproses filter opsional ini
        val response = vehiclesService.getVehiclesWithFilters(
            manufacturerId, startYear, endYear, driveType, drivetrain, pageable
        )
        return ResponseEntity.ok(response)
    }

    /**
     * 2. GET VEHICLE BY ID
     * URL: GET http://localhost:8080/api/v1/vehicles/5
     */
    @Operation(
        summary = "Get Vehicle by ID",
        description = "Retrieves detailed specifications of a car based on a unique database ID."
    )
    @GetMapping("/{id}")
    fun getVehicleById(
        @PathVariable("id") id: Int
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.getVehicleById(id)
        return ResponseEntity.ok(response)
    }

    /**
     * 3. SEARCH VEHICLE BY MODEL NAME (LIKE QUERY)
     * Menggunakan Query Param 'name' untuk pencarian teks (misal: "Skyline", "Furai").
     * URL: GET http://localhost:8080/api/v1/vehicles/search?name=Skyline
     */
    @Operation(
        summary = "Search Vehicle by Model Name (LIKE QUERY)",
        description = "Retrieves vehicles based on a partial match of the model name. Example: GET /api/v1/vehicles/search?name=Skyline"
    )
    @GetMapping("/search/{name}")
    fun searchVehiclesByModelName(
        @PathVariable("name") name: String,
        @ParameterObject
        @PageableDefault(page = 0, size = 20, sort = ["id"],direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<PageResponse<VehiclesResp>> {
        val response = vehiclesService.searchVehiclesByModelName(name, pageable)
        return ResponseEntity.ok(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createVehicle(
        @RequestBody request: VehiclesReq
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.createVehicle(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk")
    fun bulkCreate(
        @RequestBody requests: List<VehiclesReq>
    ): ResponseEntity<List<VehiclesResp>> {
        val response = vehiclesService.bulkCreate(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun updateVehicle(
        @PathVariable("id") id: Int,
        @RequestBody request: VehiclesReq
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.updateVehicle(id, request)
        return ResponseEntity.ok(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteVehicle(
        @PathVariable("id") id: Int
    ): ResponseEntity<Map<String, String>> {
        val message = vehiclesService.deleteVehicle(id)

        // Membungkus return String biasa menjadi format JSON { "message": "Success..." } agar rapi di sisi Client/Android
        val response = mapOf("message" to message)
        return ResponseEntity.ok(response)
    }
    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{vehicleId}/images")
    fun addImage(
        @PathVariable vehicleId: Int,
        @Valid @RequestBody request: VehicleImagesReq
    ): ResponseEntity<VehicleImagesResp> {
        val response = vehicleImageService.addImageToVehicle(vehicleId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{vehicleId}/images")
    fun getVehicleImages(@PathVariable vehicleId: Int): ResponseEntity<List<VehicleImagesResp>> {
        val responses = vehicleImageService.getImagesByVehicle(vehicleId)
        return ResponseEntity.ok(responses)
    }
}