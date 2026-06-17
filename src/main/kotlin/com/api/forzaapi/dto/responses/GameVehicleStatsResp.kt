package com.api.forzaapi.dto.responses

data class GameVehicleStatsResp(
    val id: Int,
    val vehicleModelName: String,
    val gameTitle: String,
    val divisionName: String?,
    val rarity: String,
    val unlockType: String,
    val performanceProfile: PerformanceProfile,
    val metrics: Map<String, Double?>,
    val dlcRequired: String?,
    val forzathonShopCost: Int?,
    val isBackstageAvailable: Boolean
)
data class PerformanceProfile(
    val className: String,
    val rating: Int?
)