package com.myapi.diningreviewapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = arrayOf("com.myapi.diningreviewapi"))
class DiningReviewApiApplication

fun main(args: Array<String>) {
    runApplication<DiningReviewApiApplication>(*args)
}
