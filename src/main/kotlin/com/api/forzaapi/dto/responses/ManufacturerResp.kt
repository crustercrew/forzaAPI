package com.api.forzaapi.dto.responses

data class ManufacturerResp(
    val id:Int,
    val name: String,
    val country: String
)

data class ManufacturerListOBJResp(
    val country: String,
    val manufacturers: List<ManufacturerOBJResp>
)

data class ManufacturerOBJResp(
    val id: Int,
    val name: String
)