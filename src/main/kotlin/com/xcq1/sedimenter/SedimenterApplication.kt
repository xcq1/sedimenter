package com.xcq1.sedimenter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class SedimenterApplication

fun main(args: Array<String>) {
    runApplication<SedimenterApplication>(*args)
}
