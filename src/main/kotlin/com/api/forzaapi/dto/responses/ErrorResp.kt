package com.api.forzaapi.dto.responses

import java.time.LocalDateTime

data class ErrorResp(
    val status:Int,
    val error: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val message: String?,
)
