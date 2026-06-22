package com.api.forzaapi.controllers


import com.api.forzaapi.dto.request.DivisionReq
import com.api.forzaapi.dto.responses.DivisionResp
import com.api.forzaapi.services.DivisionService
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
@RequestMapping("/divisions")
class DivisionController(
    private val divisionService: DivisionService
) {
    @Operation(
        summary = "Get all divisions",
        description = "receive data from database"
    )
    @GetMapping
    fun getAllDivisions(
        @RequestParam(value = "name", required = false) name: String?,
        @PageableDefault(page = 0, size = 20, sort = ["id"]) pageable: Pageable
    ): ResponseEntity<Any> {

        if (!name.isNullOrBlank()) {
            val division = divisionService.getDivisionByName(name)
            return ResponseEntity.ok(division)
        }

        val response = divisionService.getAllDivision(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Get division by id",
        description = "Get division by id"
    )
    @GetMapping("/{id}")
    fun getDivisionById(
        @PathVariable("id") id: Int
    ): ResponseEntity<DivisionResp> {
        val division = divisionService.getDivisionById(id)
        return ResponseEntity.ok(division)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createDivision(@RequestBody request: DivisionReq): ResponseEntity<DivisionResp> {
        val response = divisionService.createDivisions(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulkcreate")
    fun bulkCreateDivisions(@RequestBody requests: List<DivisionReq>): ResponseEntity<List<DivisionResp>> {
        val response = divisionService.bulkCreateDivisions(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    fun updateDivision(
        @PathParam("id") id: Int,
        @RequestBody request: DivisionReq
    ): ResponseEntity<DivisionResp> {
        return ResponseEntity.ok(divisionService.updateDivisions(id, request))
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping()
    fun deleteDivision(@PathParam("id") id: Int): ResponseEntity<Void> {
        divisionService.deleteDivisions(id)
        // Return 204 No Content kalau berhasil dihapus (standar API)
        return ResponseEntity.noContent().build()
    }
}