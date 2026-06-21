package com.api.forzaapi.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtUtils {
    private val secretKey = Keys.hmacShaKeyFor("RahasiaKunciForzaAPIYangSangatPanjangDanAmanSekali123!".toByteArray())
    private val expirationMs = 86400000

    fun generateToken(username: String, role: String): String {
        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }
    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body.subject
    }

    fun getRoleFromToken(token: String): String {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body["role"] as String
    }
}