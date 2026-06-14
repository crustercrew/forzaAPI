package com.api.forzaapi.dto.responses.manufacturers

import com.api.forzaapi.dto.responses.manufacturers.ManufacturerOBJResp

data class ManufacturerListOBJResp(
    val country: String,
    val manufacturers: List<ManufacturerOBJResp>
)
