package com.api.forzaapi.services

import com.api.forzaapi.dto.request.DivisionReq
import com.api.forzaapi.dto.responses.DivisionResp
import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Divisions
import com.api.forzaapi.repositories.DivisionRepository
import com.api.forzaapi.utils.toPageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class DivisionService(
    private val divisionRepository: DivisionRepository
) {
    fun getAllDivision(pageable: Pageable): PageResponse<DivisionResp> {
        return divisionRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    fun getDivisionByName(name: String): DivisionResp {
        val division = divisionRepository.findByNameContainingIgnoreCase(name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Car Type with $name not found")

        return division.toResponse();
    }

    // CREATE
    @Transactional
    fun createDivisions(request: DivisionReq): DivisionResp {
        val division = Divisions(
            id = 0, // ID will be auto-generated
            name = request.name,
        )
        return divisionRepository.save(division).toResponse()
    }

    @Transactional
    fun bulkCreateDivisions(requests: List<DivisionReq>): List<DivisionResp> {
        val divisions = requests.map {
            Divisions(
                id = 0,
                name = it.name
            )
        }
        return divisionRepository.saveAll(divisions).map { it.toResponse() }
    }

    // UPDATE
    @Transactional
    fun updateDivisions(id: Int, request: DivisionReq): DivisionResp {
        // Cek dulu apakah division-nya ada?
        val division = divisionRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Car Type with id $id not found")

        // Timpa data lama dengan data baru
        division.name = request.name

        return divisionRepository.save(division).toResponse()
    }

    // DELETE
    @Transactional
    fun deleteDivisions(id: Int) {
        val division = divisionRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Car Type with id $id not found")

        divisionRepository.delete(division)
    }

    private fun Divisions.toResponse(): DivisionResp {
        return DivisionResp(
            id = this.id,
            name = this.name
        )
    }
}