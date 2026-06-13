package com.api.forzaapi.entity

import jakarta.persistence.*

@Entity
class Divisions(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false, unique = true, length = 100)
    var name:String,
): BaseEntity()