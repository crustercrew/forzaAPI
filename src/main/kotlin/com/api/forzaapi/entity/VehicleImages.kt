package com.api.forzaapi.entity

import jakarta.persistence.*

@Entity
class VehicleImages (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    val vehicle: Vehicles,

    @Column(nullable = false)
    var imageUrl: String,

    @Column(name = "game_series")
    var gameimageseries:String
) : BaseEntity()