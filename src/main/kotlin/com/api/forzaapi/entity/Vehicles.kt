package com.api.forzaapi.entity

import ch.qos.logback.core.Layout
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import com.api.forzaapi.enumerates.PerformanceClass
import com.api.forzaapi.enumerates.Rarity
import com.api.forzaapi.enumerates.UniqueUnlock
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault

@Entity
class Vehicles(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column("model_name",nullable = false, length = 100)
    var modelName: String,

    @Column("production_year",nullable = false)
    var productionyear: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    var manufacturer: Manufacturers,

    @Column("engine_spec",nullable = false, length = 100)
    var enginespec: String,

    @Column("horsepower", length = 50,nullable = false)
    @ColumnDefault("0")
    var horsepower:Int,

    @Column("torque", length = 50,nullable = false)
    @ColumnDefault("0")
    var torque:Int,

    @Column("drivetype")
    @Enumerated(EnumType.STRING)
    var driveType: DriveType,

    @Column("drivetrain")
    @Enumerated(EnumType.STRING)
    var drivetrain: Drivetrain,

    @Column(length = 50)
    var transmission:String,

    @Column("weight_kg", length = 50,nullable = false)
    @ColumnDefault("0")
    var weightkg:Int,

    @Column("weight_distribution", length = 20)
    var weightdistribution:String,

    @Column("description", columnDefinition = "TEXT")
    var description:String
): BaseEntity()