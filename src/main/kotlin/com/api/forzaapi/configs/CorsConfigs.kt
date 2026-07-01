package com.api.forzaapi.configs

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfigs: WebMvcConfigurer {
    override fun addCorsMappings(registry: org.springframework.web.servlet.config.annotation.CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // Izinkan semua origin
            .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // Izinkan semua metode HTTP
            .allowedHeaders("*") // Izinkan semua header
            .allowCredentials(false) // Tidak mengizinkan kredensial (cookies, HTTP authentication)
            .maxAge(3600) // Cache CORS preflight request selama 1 jam
    }
}