package com.api.forzaapi.services

import com.api.forzaapi.dto.responses.PageResponse
import com.api.forzaapi.entity.Manufacturers
import com.api.forzaapi.repositories.ManufacturersRepository
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import com.api.forzaapi.dto.request.ManufacturerReq
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerListOBJResp
import com.api.forzaapi.dto.responses.manufacturers.ManufacturerOBJResp
import com.api.forzaapi.utils.toPageResponse
import org.springframework.data.domain.PageImpl
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
        val manufacturers = manufacturersRepository.findByNameIgnoreCase(name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer with $name not found")

        return manufacturers.toResponse();
    }

    fun getManufacturerById(id:Int): ManufacturerResp{
        val manufacturer = manufacturersRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND,"Manufacturer with $id not found")

        return manufacturer.toResponse();
    }

    fun getManufacturerByCountry(country: String,pageable: Pageable): PageResponse<ManufacturerListOBJResp> {
        val manufacturers = manufacturersRepository.findByCountryContainingIgnoreCase(country, pageable)

        if (manufacturers.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer with country $country not found")
        }

        val groupedData = manufacturers.content.groupBy { it.country }

        val mappedList = groupedData.map { (countryName, manufacturerList) ->
            ManufacturerListOBJResp(
                country = countryName,
                manufacturers = manufacturerList.map {
                    ManufacturerOBJResp(it.id, it.name)
                }
            )
        }

        val mappedPage = PageImpl(
            mappedList,
            pageable,
            manufacturers.size.toLong()
        )

        return mappedPage.toPageResponse()
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