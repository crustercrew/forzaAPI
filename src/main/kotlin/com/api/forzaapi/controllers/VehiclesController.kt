package com.api.forzaapi.controllers

import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.VehiclesResp
import com.api.forzaapi.services.VehiclesService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vehicles")
class VehiclesController(
    private val vehiclesService: VehiclesService
) {
    @GetMapping
    fun getAllVehicles(
        @PageableDefault(page = 0, size = 20, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<PageResponse<VehiclesResp>> {
        val response = vehiclesService.getAllVehicles(pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getVehicleById(
        @PathVariable("id") id: String
    ): ResponseEntity<VehiclesResp> {
        val response = vehiclesService.getVehicleById(id)
        return ResponseEntity.ok(response)
    }
}