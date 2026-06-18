package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.GameVehicleStatsReq
import com.api.forzaapi.dto.responses.GameVehicleStatsResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.services.GameVehicleStatsService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vehicle-stats")
class GameVehicleStatsController(
    private val gameVehicleStatsService: GameVehicleStatsService
) {

    /**
     * 1. GET ALL STATS (PAGINATED) & OPTIONAL FILTER BY VEHICLE ID
     * URL: GET http://localhost:8080/api/v1/vehicle-stats
     * Example: GET http://localhost:8080/api/v1/vehicle-stats?vehicleId=5
     */
//    @GetMapping
//    fun getStats(
//        @RequestParam(value = "vehicleId", required = false) vehicleId: Int?,
//        @PageableDefault(page = 0, size = 20, sort = ["id"]) pageable: Pageable
//    ): ResponseEntity<PageResponse<GameVehicleStatsResp>> {
//        val response = gameVehicleStatsService.getStats(vehicleId, pageable)
//        return ResponseEntity.ok(response)
//    }

    /**
     * 2. GET SINGLE RECORD STATS BY ID
     * URL: GET http://localhost:8080/api/v1/vehicle-stats/5
     */
//    @GetMapping("/{id}")
//    fun getStatsById(@PathVariable("id") id: Int): ResponseEntity<GameVehicleStatsResp> {
//        val response = gameVehicleStatsService.getStatsById(id)
//        return ResponseEntity.ok(response)
//    }

    /**
     * 3. Get game stats by vehicleId
     * URL: GET /api/v1/vehicle-stats/by-vehicle/{vehicleId}
     */


    /**
     * 4. Get game Stats By GameId
     * URL: GET /api/v1/vehicle-stats/by-game/{gameId}
     */


    /**
     * 5. Get Game Stats By Division or Car Type ID
     * URL: GET /api/v1/vehicle-stats/by-division/{divisionId}
     */

    /**
     * 6. CREATE SINGLE RECORD STATS
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
     * 7. BULK CREATE RECORDS STATS (INPUT MASSAL)
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
     * 8. UPDATE RECORD STATS BY ID
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
     * 9. DELETE RECORD STATS BY ID
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