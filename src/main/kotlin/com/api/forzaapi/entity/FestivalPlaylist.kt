package com.api.forzaapi.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "festival_playlists")
class FestivalPlaylist (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game, // Merujuk ke game terkait (contoh: Forza Horizon 6)

    @Column(name = "series_number", nullable = false)
    val seriesNumber: Int, // Misal: Series 32, Series 33

    @Column(name = "season", nullable = false, length = 20)
    val season: String, // "Summer", "Autumn", "Winter", "Spring", "Series Milestone"

    @Column(name = "points_required")
    val pointsRequired: Int?, // Jumlah poin yang dibutuhkan untuk klaim hadiah ini

    @Column(name = "reward_type", length = 50)
    val rewardType: String = "Car", // Default 'Car'

    @Column(name = "start_date")
    val startDate: LocalDate?,

    @Column(name = "end_date")
    val endDate: LocalDate?,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "playlist_rewards",
        joinColumns = [JoinColumn(name = "playlist_id")],
        inverseJoinColumns = [JoinColumn(name = "game_vehicle_stats_id")]
    )
    var rewards: MutableSet<GameVehicleStats> = mutableSetOf()
    // Menggunakan Set agar tidak ada data mobil duplikat di dalam satu playlist yang sama
): BaseEntity()