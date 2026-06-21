package com.api.forzaapi.controllers

import com.api.forzaapi.dto.responses.AuthResp
import com.api.forzaapi.services.JwtUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtUtils: JwtUtils,
    @Value("\${api.admin.username}") private val adminUsername: String,
    @Value("\${api.admin.password}") private val adminPassword: String
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: Map<String, String>): AuthResp {
        val username = loginRequest["username"]
        val password = loginRequest["password"]
        if (username == adminUsername && password == adminPassword) {
//            val token = jwtUtils.generateToken(username, "ADMIN")
//            mapOf("token" to token)
            return AuthResp(
                code = "00",
                message = "successfully login",
                token = jwtUtils.generateToken(username,"ADMIN")
            )
        } else {
//            throw RuntimeException("Kredensial salah bro!")
            return AuthResp(
                code = "50",
                message = "FAILED LOGIN username or password wrong",
                token = ""
            )
        }
    }
}