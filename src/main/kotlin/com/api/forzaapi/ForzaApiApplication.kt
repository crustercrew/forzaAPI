package com.api.forzaapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.io.File

@SpringBootApplication
@EnableJpaAuditing
class ForzaApiApplication

fun main(args: Array<String>) {
	runApplication<ForzaApiApplication>(*args)
}