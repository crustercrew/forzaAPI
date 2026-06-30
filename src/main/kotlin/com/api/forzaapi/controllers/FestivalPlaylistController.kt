package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.FestivalPlaylistReq
import com.api.forzaapi.dto.responses.FestivalPlaylistResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.services.FestivalPlaylistService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Festival Playlists", description = "Endpoints to query dynamic seasonal campaigns, series rotations, and live-service event rewards")
@RestController
@RequestMapping("/festival-playlist")
class FestivalPlaylistController(
    private val festivalPlaylistService: FestivalPlaylistService
) {
    @Operation(
        summary = "Browse or filter live-service seasonal festival playlists",
        description = "Returns a paginated log of festival playlists across multiple franchise iterations. " +
                "Accepts optional filters to narrow down operations by a specific Game Edition, Series Number, and Target Season Group (e.g., 'series_milestone', 'Summer'). " +
                "The output fully embeds attached dynamic junction reward arrays containing mapped vehicle telemetry stats."
    )
    @GetMapping
    fun getAllPlaylists(
        @RequestParam(value = "gameId", required = false) gameId: Int?,
        @RequestParam(value = "seriesNumber", required = false) seriesNumber: Int?,
        @RequestParam(value = "season", required = false) season: String?,
        @ParameterObject
        @PageableDefault(page = 0, size = 20, sort = ["id"],direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<PageResponse<FestivalPlaylistResp>> {
        val response = festivalPlaylistService.getAllPlaylistsWithFilters(gameId, seriesNumber, season, pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Fetch an isolated festival playlist campaign timeline by ID",
        description = "Retrieves structural database profile metrics for a precise live-service weekly or series milestone record block. " +
                "Returns flat payload properties indicating point eligibility structures, validation dates, and comprehensive nested milestone unlock rewards."
    )
    @GetMapping("/{id}")
    fun getPlaylistById(
        @PathVariable id: Int
    ): ResponseEntity<FestivalPlaylistResp> {
        val response = festivalPlaylistService.getPlaylistById(id)
        return ResponseEntity.ok(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createPlaylist(
        @RequestBody request: FestivalPlaylistReq
    ): ResponseEntity<FestivalPlaylistResp> {
        val response = festivalPlaylistService.createPlaylist(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk")
    fun bulkCreatePlaylists(
        @RequestBody requests: List<FestivalPlaylistReq>
    ): ResponseEntity<List<FestivalPlaylistResp>> {
        val response = festivalPlaylistService.bulkCreatePlaylists(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun updatePlaylist(
        @PathVariable id: Int,
        @RequestBody request: FestivalPlaylistReq
    ): ResponseEntity<FestivalPlaylistResp> {
        val response = festivalPlaylistService.updatePlaylist(id, request)
        return ResponseEntity.ok(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deletePlaylist(
        @PathVariable id: Int
    ): ResponseEntity<Map<String, String>> {
        val message = festivalPlaylistService.deletePlaylist(id)
        return ResponseEntity.ok(mapOf("message" to message))
    }
}