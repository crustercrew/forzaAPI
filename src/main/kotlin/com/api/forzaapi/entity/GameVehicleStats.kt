package com.api.forzaapi.entity

import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock
import jakarta.persistence.*

@Entity
@Table(name = "game_vehicle_stats")
class GameVehicleStats(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    val vehicle: Vehicles,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    val division: Divisions?,

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity", nullable = false)
    val rarity: Rarity,

    @Enumerated(EnumType.STRING)
    @Column(name = "unlock_type", nullable = false)
    val unlocktype: UniqueUnlock,

    @Enumerated(EnumType.STRING)
    @Column(name = "performance_class", nullable = false)
    val performanceclass: PerformanceClass,

    @Column(name = "performance_rating")
    val performancerating: Int?,

    @Column(name = "stat_speed")
    val statSpeed: Double?,

    @Column(name = "stat_handling")
    val statHandling: Double?,

    @Column(name = "stat_acceleration")
    val statAcceleration: Double?,

    @Column(name = "stat_launch")
    val statLaunch: Double?,

    @Column(name = "stat_braking")
    val statBraking: Double?,

    @Column(name = "stat_offroad")
    val statOffroad: Double?,

    @Column(name = "autoshow_cost")
    val autoshowCost: Int?,

    @Column(name = "dlc_required", length = 150)
    val dlcRequired: String?
)