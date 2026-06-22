package com.api.forzaapi.dto.responses

import tools.jackson.databind.ser.Serializers

data class PageResponse<T>(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val data: List<T>,
): Serializers