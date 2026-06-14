package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.ManufacturerReq
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerListOBJResp
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import com.api.forzaapi.services.ManufacturersService
import jakarta.websocket.server.PathParam
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manufacturers")
class ManufacturerController(
    private val manufacturerService: ManufacturersService
) {
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

    @GetMapping("/by-country")
    fun getManufacturersGroupedByCountry(
        @RequestParam(value = "country") country: String
    ): ResponseEntity<List<ManufacturerListOBJResp>> {
        val response = manufacturerService.getManufacturerByCountry(country)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createManufacturer(@RequestBody request: ManufacturerReq): ResponseEntity<ManufacturerResp> {
        val response = manufacturerService.createManufacturers(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/bulkcreate")
    fun bulkCreateManufacturers(@RequestBody requests: List<ManufacturerReq>): ResponseEntity<List<ManufacturerResp>> {
        val response = manufacturerService.bulkCreateManufacturers(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping()
    fun updateManufacturer(
        @PathParam("id") id: Int,
        @RequestBody request: ManufacturerReq
    ): ResponseEntity<ManufacturerResp> {
        return ResponseEntity.ok(manufacturerService.updateManufacturers(id, request))
    }

    @DeleteMapping()
    fun deleteManufacturer(@PathParam("id") id: Int): ResponseEntity<Void> {
        manufacturerService.deleteManufacturers(id)
        // Return 204 No Content kalau berhasil dihapus (standar API)
        return ResponseEntity.noContent().build()
    }
}