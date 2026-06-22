package com.api.forzaapi.configs

import com.api.forzaapi.utils.JwtRequestFilter
import com.api.forzaapi.services.JwtUtils
import org.springframework.http.HttpMethod
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.jvm.java

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityFilterChainConfig(
    private val jwtUtil: JwtUtils
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                // 1. Endpoint Auth (Login) bebas diakses
                auth.requestMatchers("/auth/login").permitAll()

                // 2. Buka semua request HTTP GET secara global untuk publik (opsional, tapi praktis)
                auth.requestMatchers(HttpMethod.GET).permitAll()

                // 3. Sisanya pokoknya harus bawa token (Akan dicek lebih detail Role-nya di Controller)
                auth.anyRequest().authenticated()
            }
            .addFilterBefore(JwtRequestFilter(jwtUtils = jwtUtil), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}