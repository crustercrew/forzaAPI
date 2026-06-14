package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.VehiclesReq
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehiclesResp
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
     * 1. GET ALL VEHICLES (WITH PAGINATION)
     * URL: GET http://localhost:8080/api/v1/vehicles
     */
    @GetMapping
    fun getAllVehicles(
        @PageableDefault(page = 0, size = 20, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<PageResponse<VehiclesResp>> {
        val response = vehiclesService.getAllVehicles(pageable)
        return ResponseEntity.ok(response)
    }
    /**
     * 2. GET VEHICLE BY ID
     * URL: GET http://localhost:8080/api/v1/vehicles/5
     */
    @GetMapping("/{id}")
    fun getVehicleById(
        @PathVariable("id") id: Int // Koreksi: Diubah dari String ke Int agar sinkron dengan Service
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.getVehicleById(id)
        return ResponseEntity.ok(response!!)
    }

    /**
     * 3. GET VEHICLE BY MODEL NAME
     * URL: GET http://localhost:8080/api/v1/vehicles/by-modelname?name=Furai
     */
    @GetMapping("/by-modelname")
    fun getVehicleByModelName(
        @RequestParam("name") name: String // Koreksi: Diubah dari @PathParam ke @RequestParam
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.getVehicleByModelName(name)
        return ResponseEntity.ok(response!!)
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