package com.api.forzaapi.services

import com.api.forzaapi.dto.request.VehicleImagesReq
import com.api.forzaapi.dto.responses.VehicleImagesResp
import com.api.forzaapi.entity.VehicleImages
import com.api.forzaapi.repositories.VehicleImagesRepository
import com.api.forzaapi.repositories.VehiclesRepository
import com.api.forzaapi.utils.errorhandler.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VehicleImagesService(
    private val vehicleImagesRepository: VehicleImagesRepository,
    private val vehicleRepository: VehiclesRepository
) {
    @Transactional
    fun addImageToVehicle(vehicleId: Int, request: VehicleImagesReq): VehicleImagesResp {
        // 1. Validasi: Pastikan mobilnya ada
        val vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow { ResourceNotFoundException("Car with ID $vehicleId not found") }

        // 2. Buat objek image baru dari entity-mu
        val newImage = VehicleImages(
            vehicle = vehicle,
            imageUrl = request.imageUrl,
            gameimageseries = request.gameimageseries
        )

        // 3. Save dan kembalikan response
        val savedImage = vehicleImagesRepository.save(newImage)
        return savedImage.toResponse()
    }

    // Fungsi tambahan untuk ngambil semua gambar dari 1 mobil
    @Transactional(readOnly = true)
    fun getImagesByVehicle(vehicleId: Int): List<VehicleImagesResp> {
        return vehicleImagesRepository.findByVehicleId(vehicleId)
            .map { it.toResponse() }
    }

    private fun VehicleImages.toResponse() = VehicleImagesResp(
        id = this.id,
        gameseries = this.gameimageseries,
        carimage = this.imageUrl,
    )
}