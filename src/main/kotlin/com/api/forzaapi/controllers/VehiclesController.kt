package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.VehiclesReq
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import com.api.forzaapi.services.VehiclesService
import jakarta.websocket.server.PathParam
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vehicles")
class VehiclesController(
    private val vehiclesService: VehiclesService
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
    @GetMapping
    fun getVehicles(
        @RequestParam(value = "manufacturerId", required = false) manufacturerId: Int?,
        @RequestParam(value = "startYear", required = false) startYear: Int?,
        @RequestParam(value = "endYear", required = false) endYear: Int?,
        @RequestParam(value = "driveType", required = false) driveType: DriveType?,
        @RequestParam(value = "drivetrain", required = false) drivetrain: Drivetrain?,
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
    @GetMapping("/search")
    fun searchVehiclesByModelName(
        @RequestParam("name") name: String,
        @PageableDefault(page = 0, size = 20, sort = ["id"]) pageable: Pageable
    ): ResponseEntity<PageResponse<VehiclesResp>> {
        val response = vehiclesService.searchVehiclesByModelName(name, pageable)
        return ResponseEntity.ok(response)
    }

    /**
     * 4. CREATE SINGLE VEHICLE
     * URL: POST http://localhost:8080/api/v1/vehicles
     */
    @PostMapping
    fun createVehicle(
        @RequestBody request: VehiclesReq
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.createVehicle(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 5. BULK CREATE VEHICLES (INPUT MASSAL)
     * URL: POST http://localhost:8080/api/v1/vehicles/bulk
     */
    @PostMapping("/bulk")
    fun bulkCreate(
        @RequestBody requests: List<VehiclesReq>
    ): ResponseEntity<List<VehiclesResp>> {
        val response = vehiclesService.bulkCreate(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 6. UPDATE VEHICLE BY ID
     * URL: PUT http://localhost:8080/api/v1/vehicles/5
     */
    @PutMapping("/{id}")
    fun updateVehicle(
        @PathVariable("id") id: Int,
        @RequestBody request: VehiclesReq
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.updateVehicle(id, request)
        return ResponseEntity.ok(response)
    }

    /**
     * 7. DELETE VEHICLE BY ID
     * URL: DELETE http://localhost:8080/api/v1/vehicles/5
     */
    @DeleteMapping("/{id}")
    fun deleteVehicle(
        @PathVariable("id") id: Int
    ): ResponseEntity<Map<String, String>> {
        val message = vehiclesService.deleteVehicle(id)

        // Membungkus return String biasa menjadi format JSON { "message": "Success..." } agar rapi di sisi Client/Android
        val response = mapOf("message" to message)
        return ResponseEntity.ok(response)
    }
}