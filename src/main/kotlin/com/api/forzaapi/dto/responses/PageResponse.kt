package com.api.forzaapi.dto.responses

data class PageResponse<T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)