package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.GameReq
import com.api.forzaapi.dto.responses.GameResp
import com.api.forzaapi.services.GameService
import org.springframework.data.web.PageableDefault
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.websocket.server.PathParam
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "Games", description = "Endpoints to fetch target game editions, launch metadata tracking, and franchise title directories")
@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService
) {
    @Operation(
        summary = "Browse global game ecosystem catalog or filter by explicit title match",
        description = "Provides a unified lookup gate. By default, returns a paginated dataset of all franchise releases (Horizon & Motorsport series) mapped with their respective historical release years. " +
                "If the optional 'title' query parameter is attached, it targets a singular exact entry from the database. " +
                "Backed up efficiently by a top-tier Redis caching layer."
    )
    @GetMapping
    fun getGames(
        @RequestParam(value = "title", required = false) title: String?,
        @ParameterObject
        @PageableDefault(page = 0, size = 20, sort = ["id"],direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Any> {

        val response = if (!title.isNullOrBlank()) {
            gameService.getGameByTitle(title)
        } else {
            gameService.getAllGames(pageable)
        }

        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Fetch a standalone game franchise profile by precise key constraint",
        description = "Retrieves structural index profile metadata for a specific game installment (e.g., 'Forza Horizon 5') via its unique primary database id. Highly optimized with immediate Redis key-value memory retrieval."
    )
    @GetMapping("/{id}")
    fun getGameById(
        @PathVariable("id") id: Int
    ): ResponseEntity<GameResp> {
        val game = gameService.getGameById(id)
        return ResponseEntity.ok(game)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createGame(@RequestBody request: GameReq): ResponseEntity<GameResp> {
        val response = gameService.createGame(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    fun updateGame(
        @PathParam("id") id: Int,
        @RequestBody request: GameReq
    ): ResponseEntity<GameResp> {
        return ResponseEntity.ok(gameService.updateGame(id, request))
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping()
    fun deleteGame(@PathParam("id") id: Int): ResponseEntity<Void> {
        gameService.deleteGame(id)
        // Return 204 No Content kalau berhasil dihapus (standar API)
        return ResponseEntity.noContent().build()
    }
}