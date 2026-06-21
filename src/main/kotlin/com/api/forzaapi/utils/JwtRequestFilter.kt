package com.api.forzaapi.utils

import com.api.forzaapi.services.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtRequestFilter(
    private val jwtUtils: JwtUtils
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        // JIKA request tidak bawa token (seperti GET publik), LANGSUNG loloskan ke filter selanjutnya!
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return // Kunci sukses: hentikan eksekusi filter ini, jangan biarkan masuk ke logic di bawah
        }

        val token = authHeader.substring(7)
        try {
            if (jwtUtils.validateToken(token)) {
                val username = jwtUtils.getUsernameFromToken(token)
                val role = jwtUtils.getRoleFromToken(token)

                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                val authentication = UsernamePasswordAuthenticationToken(username, null, authorities).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            // Jika token invalid/expired, biarkan Spring Security yang menolak lewat konfigurasi HTTP, jangan di-crash di sini
            logger.error("JWT Token failed to process: ${e.message}")
        }

        filterChain.doFilter(request, response)
    }
}