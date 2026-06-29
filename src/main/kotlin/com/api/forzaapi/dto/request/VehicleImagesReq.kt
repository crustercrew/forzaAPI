package com.api.forzaapi.dto.request

import jakarta.validation.constraints.NotBlank

data class VehicleImagesReq(
    @field:NotBlank(message = "Image URL tidak boleh kosong")
    val imageUrl: String,
    val gameimageseries: String
)
