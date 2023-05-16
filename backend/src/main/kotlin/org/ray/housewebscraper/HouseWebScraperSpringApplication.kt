package org.ray.housewebscraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class HouseWebScraperSpringApplication

fun main(args: Array<String>) {
    runApplication<HouseWebScraperSpringApplication>(*args)
}
