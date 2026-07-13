package com.api.forzaapi.dto.responses

import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock

data class GameVehicleStatsResp(
    val id: Int,
    val game: GameResp,
    val division: DivisionResp?,
    val vehicle: VehiclesResp,
    val rarity: Rarity,
    val unlockType: UniqueUnlock,
    val performanceClass: PerformanceClass,
    val performanceRating: Int?,
    val stats: VehicleMetricsResp,
    val acquisition: VehicleAcquisitionResp
)