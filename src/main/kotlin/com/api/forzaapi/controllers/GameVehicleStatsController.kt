package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.GameVehicleStatsReq
import com.api.forzaapi.dto.responses.GameVehicleStatsResp
import com.api.forzaapi.services.GameVehicleStatsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vehicle-stats")
class GameVehicleStatsController(
    private val gameVehicleStatsService: GameVehicleStatsService
) {
    /**
     * 1. CREATE SINGLE RECORD STATS
     * URL: POST http://localhost:8080/api/v1/vehicle-stats
     */
    @PostMapping
    fun createStats(
        @RequestBody request: GameVehicleStatsReq
    ): ResponseEntity<GameVehicleStatsResp> {
        val response = gameVehicleStatsService.createStats(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 2. BULK CREATE RECORDS STATS (INPUT MASSAL)
     * URL: POST http://localhost:8080/api/v1/vehicle-stats/bulk
     */
    @PostMapping("/bulk")
    fun bulkCreateStats(
        @RequestBody requests: List<GameVehicleStatsReq>
    ): ResponseEntity<List<GameVehicleStatsResp>> {
        val response = gameVehicleStatsService.bulkCreateStats(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 3. UPDATE RECORD STATS BY ID
     * URL: PUT http://localhost:8080/api/v1/vehicle-stats/5
     */
    @PutMapping("/{id}")
    fun updateStats(
        @PathVariable("id") id: Int,
        @RequestBody request: GameVehicleStatsReq
    ): ResponseEntity<GameVehicleStatsResp> {
        val response = gameVehicleStatsService.updateStats(id, request)
        return ResponseEntity.ok(response)
    }

    /**
     * 4. DELETE RECORD STATS BY ID
     * URL: DELETE http://localhost:8080/api/v1/vehicle-stats/5
     */
    @DeleteMapping("/{id}")
    fun deleteStats(
        @PathVariable("id") id: Int
    ): ResponseEntity<Map<String, String>> {
        val message = gameVehicleStatsService.deleteStats(id)
        val response = mapOf("message" to message)
        return ResponseEntity.ok(response)
    }
}