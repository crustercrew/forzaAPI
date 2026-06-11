package com.api.forzaapi.entity

import jakarta.persistence.*

@Entity
@Table(name = "pi_car_class")
class PiCarClass (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Int,

    @Column(nullable = false, unique = true, length = 100)
    val name:String
)