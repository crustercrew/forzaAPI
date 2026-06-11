package com.api.forzaapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class ForzaApiApplication

fun main(args: Array<String>) {
	runApplication<ForzaApiApplication>(*args)
}
