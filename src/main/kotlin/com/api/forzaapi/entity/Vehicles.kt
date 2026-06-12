package com.api.forzaapi.entity

import ch.qos.logback.core.Layout
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock
import jakarta.persistence.*

@Entity
class Vehicles(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column("model_name",nullable = false, length = 100)
    val modelName: String,

    @Column("production_year",nullable = false)
    val productionyear: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    val manufacturer: Manufacturers,

    @Column("engine_spec",nullable = false, length = 100)
    val enginespec: String,

    @Column("horsepower", length = 50)
    val horsepower:Int,

    @Column("torque", length = 50)
    val torque:Int,

    @Column("drivetype")
    @Enumerated(EnumType.STRING)
    val driveType: DriveType,

    @Column("drivetrain")
    @Enumerated(EnumType.STRING)
    val drivetrain: Drivetrain,

    @Column(length = 50)
    val transmission:String,

    @Column("weight_kg")
    val weightkg:Int,

    @Column("weight_distribution", length = 20)
    val weightdistribution:String,

    @Column()
    @Lob
    val description:String
)