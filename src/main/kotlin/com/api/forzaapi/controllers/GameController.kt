package com.api.forzaapi.controllers

import com.api.forzaapi.dto.request.GameReq
import com.api.forzaapi.dto.responses.GameResp
import com.api.forzaapi.services.GameService
import org.springframework.data.web.PageableDefault
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Game
import jakarta.websocket.server.PathParam
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService
) {
    @GetMapping
    fun getAllGames(
        @RequestParam(value = "title", required = false) title: String?,
        @PageableDefault(page = 0, size = 20, sort = ["id"]) pageable: Pageable
    ): ResponseEntity<Any> {

        if (!title.isNullOrBlank()) {
            val game = gameService.getGameByTitle(title)
            return ResponseEntity.ok(game)
        }

        val response = gameService.getAllGames(pageable)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createGame(@RequestBody request: GameReq): ResponseEntity<GameResp> {
        val response = gameService.createGame(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping()
    fun updateGame(
        @PathParam("id") id: Int,
        @RequestBody request: GameReq
    ): ResponseEntity<GameResp> {
        return ResponseEntity.ok(gameService.updateGame(id, request))
    }

    @DeleteMapping()
    fun deleteGame(@PathParam("id") id: Int): ResponseEntity<Void> {
        gameService.deleteGame(id)
        // Return 204 No Content kalau berhasil dihapus (standar API)
        return ResponseEntity.noContent().build()
    }
}