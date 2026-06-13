package com.api.forzaapi.services

import com.api.forzaapi.dto.request.GameReq
import com.api.forzaapi.dto.responses.GameResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Game
import com.api.forzaapi.repositories.GameRepository
import com.api.forzaapi.utils.toPageResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.data.repository.findByIdOrNull

@Service
class GameService(
    private val gameRepository: GameRepository
) {
    fun getAllGames(pageable: Pageable): PageResponse<GameResp> {
        return gameRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    fun getGameByTitle(title: String): GameResp {
        val game = gameRepository.findByTitleContainingIgnoreCase(title)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game with title $title not found")

        return game.toResponse();
    }

    // CREATE
    @Transactional
    fun createGame(request: GameReq): GameResp {
        val game = Game(
            id = 0, // ID will be auto-generated
            title = request.title,
            releaseYear = request.releaseYear
        )
        return gameRepository.save(game).toResponse()
    }

    // UPDATE
    @Transactional
    fun updateGame(id: Int, request: GameReq): GameResp {
        // Cek dulu apakah game-nya ada?
        val game = gameRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id $id not found")

        // Timpa data lama dengan data baru
        game.title = request.title
        game.releaseYear = request.releaseYear

        return gameRepository.save(game).toResponse()
    }

    // DELETE
    @Transactional
    fun deleteGame(id: Int) {
        val game = gameRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id $id not found")

        gameRepository.delete(game)
    }

    private fun Game.toResponse(): GameResp {
        return GameResp(
            id = this.id,
            title = this.title,
            releaseYear = this.releaseYear
        )
    }
}
