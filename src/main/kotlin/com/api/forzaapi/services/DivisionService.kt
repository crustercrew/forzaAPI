package com.api.forzaapi.services

import com.api.forzaapi.repositories.DivisionRepository
import org.springframework.stereotype.Service

@Service
class DivisionService(
    private val divisionRepository: DivisionRepository
) {
    fun getAllDivisions() = divisionRepository.findAll()

    fun getDivisionByName(name: String) = divisionRepository.findByName(name)
}