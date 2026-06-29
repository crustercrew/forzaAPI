package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.GameReq
import com.api.forzaapi.dto.responses.GameResp
import com.api.forzaapi.services.GameService
import org.springframework.data.web.PageableDefault
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Game
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import jakarta.websocket.server.PathParam
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService
) {
    @Operation(
        summary = "Get all games",
        description = "Get all games"
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