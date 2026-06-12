package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game,String> {
    fun findByName(name: String): Game?
}