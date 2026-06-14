package com.api.forzaapi.dto.responses

import java.time.LocalDate

data class FestivalPlaylistResp(
    val id: Int,
    val gameTitle: String,
    val seriesNumber: Int,
    val season: String,
    val pointsRequired: Int?,
    val rewardType: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val rewards: List<PlaylistRewardVehicle>
)