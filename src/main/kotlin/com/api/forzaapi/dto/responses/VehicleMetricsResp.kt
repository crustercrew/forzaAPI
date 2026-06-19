package com.api.forzaapi.dto.responses

data class VehicleMetricsResp(
    val speed: Double?,
    val handling: Double?,
    val acceleration: Double?,
    val launch: Double?,
    val braking: Double?,
    val offroad: Double?
)
