package com.api.forzaapi.utils
import com.api.forzaapi.dto.responses.PageResponse
import org.springframework.data.domain.Page

    fun <T : Any> Page<T>.toPageResponse(): PageResponse<T> {
        return PageResponse(
            data = this.content,
            page = this.number,
            size = this.size,
            totalElements = this.totalElements,
            totalPages = this.totalPages
        )
    }