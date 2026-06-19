package com.api.forzaapi.dto.responses

import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock

data class GameVehicleStatsResp(
    val id: Int,
    val game: GameResp,
    val division: DivisionResp?, // Nullable karena bisa saja ada entitas tanpa divisi di game tertentu
    val vehicle: VehiclesResp,    // Mengandung spesifikasi fisik lengkap sesuai katalog global
    val rarity: Rarity,
    val unlockType: UniqueUnlock,
    val performanceClass: PerformanceClass,
    val performanceRating: Int?,
    val stats: VehicleMetricsResp,     // Dikelompokkan agar rapi saat digambar jadi grafik di Android
    val acquisition: VehicleAcquisitionResp
)