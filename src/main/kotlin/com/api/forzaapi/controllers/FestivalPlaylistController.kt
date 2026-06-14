package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.FestivalPlaylistReq
import com.api.forzaapi.dto.responses.FestivalPlaylistResp
import com.api.forzaapi.services.FestivalPlaylistService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/festival-playlist")
class FestivalPlaylistController(
    private val festivalPlaylistService: FestivalPlaylistService
) {
    @PostMapping
    fun createPlaylist(
        @RequestBody request: FestivalPlaylistReq
    ): ResponseEntity<FestivalPlaylistResp> {
        val response = festivalPlaylistService.createPlaylist(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/bulk")
    fun bulkCreatePlaylists(
        @RequestBody requests: List<FestivalPlaylistReq>
    ): ResponseEntity<List<FestivalPlaylistResp>> {
        val response = festivalPlaylistService.bulkCreatePlaylists(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun updatePlaylist(
        @PathVariable id: Int,
        @RequestBody request: FestivalPlaylistReq
    ): ResponseEntity<FestivalPlaylistResp> {
        val response = festivalPlaylistService.updatePlaylist(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deletePlaylist(
        @PathVariable id: Int
    ): ResponseEntity<Map<String, String>> {
        val message = festivalPlaylistService.deletePlaylist(id)
        return ResponseEntity.ok(mapOf("message" to message))
    }
}