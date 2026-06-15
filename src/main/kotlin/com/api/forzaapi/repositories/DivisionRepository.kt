package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Divisions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DivisionRepository: JpaRepository<Divisions,Int> {
    fun findByNameIgnoreCase(name: String): Divisions?
}