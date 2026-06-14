package com.api.forzaapi.dto.request

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class FestivalPlaylistReq(
    @field:NotBlank(message = "Game ID is required")
    val gameId: Int,
    @field:NotBlank(message = "Series Number is required")
    val seriesNumber: Int,
    @field:NotBlank(message = "Season is required")
    val season: String,
    val pointsRequired: Int?,
    val rewardType: String = "Car",
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val rewardVehicleStatsIds: List<Int> = emptyList()
)