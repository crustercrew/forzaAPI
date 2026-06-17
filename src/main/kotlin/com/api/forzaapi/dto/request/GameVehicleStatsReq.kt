package com.api.forzaapi.dto.request

import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock
import jakarta.validation.constraints.NotBlank

data class GameVehicleStatsReq (
    @field:NotBlank(message = "Vehicle ID is required")
    val vehicleId: Int,
    @field:NotBlank(message = "Game ID is required")
    val gameId: Int,
    val divisionId: Int?,
    val rarity: Rarity,
    val unlocktype: UniqueUnlock,
    val performanceclass: PerformanceClass,
    val performancerating: Int?,

    val statSpeed: Double?,
    val statHandling: Double?,
    val statAcceleration: Double?,
    val statLaunch: Double?,
    val statBraking: Double?,
    val statOffroad: Double?,

    val autoshowCost: Int?,
    val dlcRequired: String?,
    val forzathonShopCost: Int?,
    var isBackstageAvailable: Boolean = false
)