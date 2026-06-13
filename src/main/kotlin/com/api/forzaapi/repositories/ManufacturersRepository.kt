package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Manufacturers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ManufacturersRepository: JpaRepository<Manufacturers,String> {
    fun findByName(name:String): Manufacturers?
    fun findByCountry(country: String): List<Manufacturers>
}