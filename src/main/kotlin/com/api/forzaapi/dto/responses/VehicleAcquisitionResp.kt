package com.api.forzaapi.dto.responses

data class VehicleAcquisitionResp(
    val autoshowCost: Int?,
    val forzathonShopCost: Int?,
    val isBackstageAvailable: Boolean,
    val dlcRequired: String?
)
