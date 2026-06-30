package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.ManufacturerReq
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.dto.responses.ManufacturerListOBJResp
import com.api.forzaapi.dto.responses.ManufacturerResp
import com.api.forzaapi.services.ManufacturersService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.websocket.server.PathParam
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "Manufacturers", description = "Endpoints to query global automotive brands and country-of-origin master directories")
@RestController
@RequestMapping("/manufacturers")
class ManufacturerController(
    private val manufacturerService: ManufacturersService
) {
    @Operation(
        summary = "Browse or search automotive manufacturers matrix",
        description = "Returns a paginated directory of registered car brands alongside their geographical legal headquarters. " +
                "Accepts an optional query filter to narrow down results by case-insensitive partial brand names. " +
                "Results are globally cached in memory via the absolute Redis layer."
    )
    @GetMapping
    fun getAllManufacturers(
        @RequestParam(value = "name", required = false) name: String?,
        @PageableDefault(page = 0, size = 20, sort = ["id"],direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Any> {
        if (!name.isNullOrBlank()) {
            val manufacturer = manufacturerService.getManufacturerByName(name)
            return ResponseEntity.ok(manufacturer)
        }

        val response = manufacturerService.getAllManufacturers(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Fetch a targeted manufacturer profile by unique database key",
        description = "Retrieves isolated registry profile data for a specific automotive group using its strict database ID lookup constraint. Optimized via standard Redis key-value serialization mapping."
    )
    @GetMapping("/{id}")
    fun getManufacturerById(
        @PathVariable("id") id: Int
    ): ResponseEntity<ManufacturerResp> {
        val manufacturer = manufacturerService.getManufacturerById(id)
        return ResponseEntity.ok(manufacturer)
    }

    @Operation(
        summary = "Retrieve manufacturers grouped by production country matrices",
        description = "Executes an aggregated relational grouping query that nests registered car brands inside their matching sovereign country origins. " +
                "Primarily engineered to feed multi-tier filter components, dropdown lists, or regional garage UI dashboards on client-side application integrations."
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