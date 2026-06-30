package com.api.forzaapi.services

import com.api.forzaapi.dto.request.GameReq
import com.api.forzaapi.dto.responses.GameResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Game
import com.api.forzaapi.repositories.GameRepository
import com.api.forzaapi.utils.errorhandler.ResourceNotFoundException
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
    @Transactional(readOnly = true)
    fun getAllGames(pageable: Pageable): PageResponse<GameResp> {
        return gameRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    @Transactional(readOnly = true)
    fun getGameByTitle(title: String): GameResp {
        val game = gameRepository.findByTitleIgnoreCase(title)
            ?: throw ResourceNotFoundException("Game with title $title not found")

        return game.toResponse();
    }

    @Transactional(readOnly = true)
    fun getGameById(id:Int): GameResp{
        val game = gameRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Game with id $id not found")

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
        val game = gameRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Game with id $id not found")
        game.title = request.title
        game.releaseYear = request.releaseYear

        return gameRepository.save(game).toResponse()
    }

    // DELETE
    @Transactional
    fun deleteGame(id: Int) {
        val game = gameRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Game with id $id not found")

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
