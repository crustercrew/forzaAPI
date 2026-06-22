package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.ManufacturerReq
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerListOBJResp
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import com.api.forzaapi.services.ManufacturersService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import jakarta.websocket.server.PathParam
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manufacturers")
class ManufacturerController(
    private val manufacturerService: ManufacturersService
) {
    @Operation(
        summary = "Get all manufacturers",
        description = "Get all manufacturers"
    )
    @GetMapping
    fun getAllManufacturers(
        @RequestParam(value = "name", required = false) name: String?,
        @PageableDefault(page = 0, size = 20, sort = ["id"]) pageable: Pageable
    ): ResponseEntity<Any> {
        if (!name.isNullOrBlank()) {
            val manufacturer = manufacturerService.getManufacturerByName(name)
            return ResponseEntity.ok(manufacturer)
        }

        val response = manufacturerService.getAllManufacturers(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Get manufacturer by id",
        description = "Get manufacturer by id"
    )
    @GetMapping("/{id}")
    fun getManufacturerById(
        @PathVariable("id") id: Int
    ): ResponseEntity<ManufacturerResp> {
        val manufacturer = manufacturerService.getManufacturerById(id)
        return ResponseEntity.ok(manufacturer)
    }

    @Operation(
        summary = "Get manufacturers by country",
        description = "Get manufacturers by country"
    )
    @GetMapping("/by-country")
    fun getManufacturersGroupedByCountry(
        @RequestParam(value = "country") country: String,
        @PageableDefault(page = 0, size = 20, sort = ["country"]) pageable: Pageable
    ): ResponseEntity<PageResponse<ManufacturerListOBJResp>> {
        val response = manufacturerService.getManufacturerByCountry(country, pageable)
        return ResponseEntity.ok(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createManufacturer(@RequestBody request: ManufacturerReq): ResponseEntity<ManufacturerResp> {
        val response = manufacturerService.createManufacturers(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulkcreate")
    fun bulkCreateManufacturers(@RequestBody requests: List<ManufacturerReq>): ResponseEntity<List<ManufacturerResp>> {
        val response = manufacturerService.bulkCreateManufacturers(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    fun updateManufacturer(
        @PathParam("id") id: Int,
        @RequestBody request: ManufacturerReq
    ): ResponseEntity<ManufacturerResp> {
        return ResponseEntity.ok(manufacturerService.updateManufacturers(id, request))
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping()
    fun deleteManufacturer(@PathParam("id") id: Int): ResponseEntity<Void> {
        manufacturerService.deleteManufacturers(id)
        return ResponseEntity.noContent().build()
    }
}