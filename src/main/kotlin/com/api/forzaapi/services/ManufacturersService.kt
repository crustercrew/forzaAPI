package com.api.forzaapi.services

import com.api.forzaapi.entity.Manufacturers
import com.api.forzaapi.repositories.ManufacturersRepository
import org.springframework.stereotype.Service

@Service
class ManufacturersService(
    private val manufacturersRepository: ManufacturersRepository
) {
    fun getAllManufacturers(): List<Manufacturers>?{
        return manufacturersRepository.findAll();
    }
    fun getManufacturerByName(name: String): Manufacturers? {
        return manufacturersRepository.findByName(name)
    }
    fun getManufacturerByCountry(country: String):List<Manufacturers>?{
        return manufacturersRepository.findByCountry(country)

    }
}