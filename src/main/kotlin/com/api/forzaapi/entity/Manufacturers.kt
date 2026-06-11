package com.api.forzaapi.entity

import jakarta.persistence.*

@Entity
class Manufacturers(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false, unique = true, length = 100)
    val name:String,

    @Column(length = 100)
    val country:String
)