package com.api.forzaapi.services

import com.api.forzaapi.dto.request.VehicleImagesReq
import com.api.forzaapi.dto.responses.VehicleImagesResp
import com.api.forzaapi.entity.VehicleImages
import com.api.forzaapi.repositories.VehicleImagesRepository
import com.api.forzaapi.repositories.VehiclesRepository
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
            .orElseThrow { Exception("Mobil dengan ID $vehicleId tidak ditemukan") } // Ganti dengan custom exception kamu kalau ada

        // 2. Buat objek image baru dari entity-mu
        val newImage = VehicleImages(
            vehicle = vehicle,
            imageUrl = request.imageUrl
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
        images = this.imageUrl
    )
}