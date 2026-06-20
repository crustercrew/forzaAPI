package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.GameVehicleStatsReq
import com.api.forzaapi.dto.responses.GameVehicleStatsResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.services.GameVehicleStatsService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.beans.PropertyEditorSupport
import java.util.Locale
import java.util.Locale.getDefault

@RestController
@RequestMapping("/vehicle-stats")
class GameVehicleStatsController(
    private val gameVehicleStatsService: GameVehicleStatsService
) {
    // KUNCI PENGAMAN VALIDASI ENUM OTOMATIS
    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        // 1. Pengaman untuk DriveType Enum
        binder.registerCustomEditor(DriveType::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String?) {
                if (text.isNullOrBlank()) {
                    value = null
                } else {
                    try {
                        // Otomatis di-uppercase agar ?drivetype=awd (huruf kecil) tidak bikin crash 400
                        value = DriveType.valueOf(text.uppercase().trim())
                    } catch (e: IllegalArgumentException) {
                        // Jika nilainya ngawur (tidak ada di enum), set jadi null agar kueri database mengabaikan filter ini
                        value = null
                    }
                }
            }
        })

        // 2. Pengaman untuk PerformanceClass Enum
        binder.registerCustomEditor(PerformanceClass::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String?) {
                if (text.isNullOrBlank()) {
                    value = null
                } else {
                    try {
                        value = PerformanceClass.valueOf(text.uppercase().trim())
                    } catch (e: IllegalArgumentException) {
                        value = null
                    }
                }
            }
        })

        // 3. Pengaman untuk Rarity Enum (Jika diperlukan)
        binder.registerCustomEditor(Rarity::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String?) {
                if (text.isNullOrBlank()) {
                    value = null
                } else {
                    try {
                        value = Rarity.valueOf(text.uppercase(getDefault()).trim())
                    } catch (e: IllegalArgumentException) {
                        value = null
                    }
                }
            }
        })
    }

    /**
     * 1. GET ALL STATS (PAGINATED) & OPTIONAL FILTER BY VEHICLE ID
     * URL: GET http://localhost:8080/api/v1/vehicle-stats
     * Example: GET http://localhost:8080/api/v1/vehicle-stats?vehicleId=5
     */
    @GetMapping
    fun getStats(
        @RequestParam(value = "vehicleid", required = false) vehicleId: Int?,
        @RequestParam(value = "manufacturerid", required = false) manufacturerid: Int?,
        @RequestParam(value = "divisionid", required = false) divisionid: Int?,
        @RequestParam(value = "gameid", required = false) gameid: Int?,
        @RequestParam(value = "rarity", required = false) rarities: Rarity?,
        @RequestParam(value = "drivetype", required = false) drivetype: DriveType?,
        @RequestParam(value = "performanceclass", required = false) performanceclass: PerformanceClass?,
        @PageableDefault(page = 0, size = 20, sort = ["id"]) pageable: Pageable
    ): ResponseEntity<PageResponse<GameVehicleStatsResp>> {
        val response = gameVehicleStatsService.getStats(
            vehicleId,
            manufacturerid,
            divisionid,
            gameid,
            rarities,
            drivetype,
            performanceclass,
            pageable
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{vehiclestatsId}")
    fun getStatsByVehicleId(
        @PathVariable("vehiclestatsId")vehiclestatsId: Int
    ): ResponseEntity<GameVehicleStatsResp> {
        val resp =  gameVehicleStatsService.getStatsById(vehiclestatsId)
        return ResponseEntity.ok(resp)
    }
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