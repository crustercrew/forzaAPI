package com.api.forzaapi.entity

import jakarta.persistence.*

@Entity
class Game (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Int,
    @Column(nullable = false, unique = true, length = 100)
    var title:String,
    @Column("release_year")
    var releaseYear:Int? = null
): BaseEntity()
