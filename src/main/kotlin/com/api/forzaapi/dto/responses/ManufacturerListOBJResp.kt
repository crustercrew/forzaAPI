package com.api.forzaapi.dto.responses

data class ManufacturerListOBJResp(
    val country: String,
    val manufacturers: List<ManufacturerOBJResp>
)
