package com.api.forzaapi.dto.responses

data class AuthResp(
    val code: String,
    val message: String,
    val token: String?
)
