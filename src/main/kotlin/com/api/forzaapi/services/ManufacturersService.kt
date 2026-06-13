package com.api.forzaapi.services

import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Manufacturers
import com.api.forzaapi.repositories.ManufacturersRepository
import com.api.forzaapi.dto.responses.ManufacturerResp
import com.api.forzaapi.dto.request.ManufacturerReq
import com.api.forzaapi.dto.responses.ManufacturerListOBJResp
import com.api.forzaapi.utils.toPageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ManufacturersService(
    private val manufacturersRepository: ManufacturersRepository
) {
    fun getAllManufacturers(pageable: Pageable): PageResponse<ManufacturerResp> {
        return manufacturersRepository.findAll(pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }

    fun getManufacturerByName(name: String): ManufacturerResp {
        val manufacturers = manufacturersRepository.findByNameContainingIgnoreCase(name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer with $name not found")

        return manufacturers.toResponse();
    }

    fun getManufacturerByCountry(country: String): List<ManufacturerListOBJResp> {
        val manufacturers = manufacturersRepository.findByCountryContainingIgnoreCase(country)

        if (manufacturers.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer with country $country not found")
        }

        return manufacturers.groupBy { it.country }
            .map { (countryName, manufacturerList) ->
                ManufacturerListOBJResp(
                    country = countryName,
                    manufacturers = manufacturerList.map {
                        com.api.forzaapi.dto.responses.ManufacturerOBJResp(it.id, it.name)
                    }
                )
            }
    }

    // CREATE
    @Transactional
    fun createManufacturers(request: ManufacturerReq): ManufacturerResp {
        val manufacturers = Manufacturers(
            id = 0, // ID will be auto-generated
            name = request.name,
            country = request.country
        )
        return manufacturersRepository.save(manufacturers).toResponse()
    }

    // BULK CREATE
    @Transactional
    fun bulkCreateManufacturers(requests: List<ManufacturerReq>): List<ManufacturerResp> {
        val manufacturers = requests.map {
            Manufacturers(
                id = 0,
                name = it.name,
                country = it.country
            )
        }
        return manufacturersRepository.saveAll(manufacturers).map { it.toResponse() }
    }

    // UPDATE
    @Transactional
    fun updateManufacturers(id: Int, request: ManufacturerReq): ManufacturerResp {
        // Cek dulu apakah manufacturers-nya ada?
        val manufacturers = manufacturersRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer with id $id not found")

        // Timpa data lama dengan data baru
        manufacturers.name = request.name
        manufacturers.country = request.country

        return manufacturersRepository.save(manufacturers).toResponse()
    }

    // DELETE
    @Transactional
    fun deleteManufacturers(id: Int) {
        val manufacturers = manufacturersRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer with id $id not found")

        manufacturersRepository.delete(manufacturers)
    }

    private fun Manufacturers.toResponse(): ManufacturerResp {
        return ManufacturerResp(
            id = this.id,
            name = this.name,
            country = this.country
        )
    }
}