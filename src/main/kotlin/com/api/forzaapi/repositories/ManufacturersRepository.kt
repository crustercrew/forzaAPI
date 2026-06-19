package com.api.forzaapi.repositories

import com.api.forzaapi.entity.Manufacturers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ManufacturersRepository: JpaRepository<Manufacturers,Int> {
    fun findByNameIgnoreCase(name:String): Manufacturers?
    fun findByCountryContainingIgnoreCase(country: String): List<Manufacturers>
}