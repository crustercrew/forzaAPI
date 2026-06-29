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
    var vehicle: Vehicles,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    var division: Divisions?,

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity", nullable = false)
    var rarity: Rarity,

    @Enumerated(EnumType.STRING)
    @Column(name = "unlock_type", nullable = false)
    var unlocktype: UniqueUnlock,

    @Enumerated(EnumType.STRING)
    @Column(name = "performance_class", nullable = false)
    var performanceclass: PerformanceClass,

    @Column(name = "performance_rating")
    var performancerating: Int?,

    @Column(name = "stat_speed")
    var statSpeed: Double?,

    @Column(name = "stat_handling")
    var statHandling: Double?,

    @Column(name = "stat_acceleration")
    var statAcceleration: Double?,

    @Column(name = "stat_launch")
    var statLaunch: Double?,

    @Column(name = "stat_braking")
    var statBraking: Double?,

    @Column(name = "stat_offroad")
    var statOffroad: Double?,

    @Column(name = "autoshow_cost")
    var autoshowCost: Int?,

    @Column(name = "dlc_required", length = 150)
    var dlcRequired: String?,

    // Kolom Horizon Backstage (Pakai Boolean dengan default false) ini digunakan jika forza horizon nya sudah end of service persiapan forza horizon baru
    @Column(name = "is_backstage_available", nullable = false)
    var isBackstageAvailable: Boolean = false
): BaseEntity()