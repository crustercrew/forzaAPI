package com.api.forzaapi.configs

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.UUID

@Component
class GlobalHttpLoggingFilter: OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(GlobalHttpLoggingFilter::class.java)

    private val DEFAULT_CACHE_LIMIT = 10240

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (path.contains("swagger") || path.contains("api-docs") || path.contains("actuator")) {
            filterChain.doFilter(request, response)
            return
        }

        val traceId = UUID.randomUUID().toString()
        MDC.put("trace_id", traceId)

        val requestWrapper = ContentCachingRequestWrapper(request, DEFAULT_CACHE_LIMIT)
        val responseWrapper = ContentCachingResponseWrapper(response)

        val startTime = System.currentTimeMillis()

        try {
            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            val duration = System.currentTimeMillis() - startTime

            val requestPayload = getPayload(requestWrapper.contentAsByteArray, request.characterEncoding)
            val responsePayload = getPayload(responseWrapper.contentAsByteArray, response.characterEncoding)

            log.info(
                "HTTP {} {} | Status: {} | Duration: {}ms | RequestBody: {} | ResponseBody: {}",
                request.method,
                path,
                responseWrapper.status,
                duration,
                requestPayload.ifBlank { "EMPTY" },
                responsePayload.ifBlank { "EMPTY" }
            )

            responseWrapper.copyBodyToResponse()

            MDC.clear()
        }
    }

    private fun getPayload(buf: ByteArray, encoding: String?): String {
        if (buf.isEmpty()) return ""
        return try {
            val charset = encoding ?: "UTF-8"
            String(buf, 0, buf.size, charset(charset))
                .replace(Regex("\\s+"), " ") // Hapus spasi berlebih/indentasi baris baru agar tetap satu baris JSON
        } catch (e: Exception) {
            "[NON-PARSEABLE PAYLOAD]"
        }
    }
}