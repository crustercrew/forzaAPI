package com.api.forzaapi.services

import com.api.forzaapi.repositories.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService(
    private val gameRepository: GameRepository
) {
    fun getAllGames() = gameRepository.findAll()

    fun getGameByTitle(title: String) = gameRepository.findByTitle(title)
}