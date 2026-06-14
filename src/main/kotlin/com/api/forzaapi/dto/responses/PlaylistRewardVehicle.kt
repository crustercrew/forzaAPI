package com.api.forzaapi.dto.responses

data class PlaylistRewardVehicle(
    val gameVehicleStatsId: Int,
    val modelName: String,
    val performanceClass: String,
    val performanceRating: Int?
)
