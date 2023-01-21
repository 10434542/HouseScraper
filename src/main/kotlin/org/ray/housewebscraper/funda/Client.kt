package org.ray.housewebscraper.funda

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class Client(val template: RestTemplate) {
    suspend fun getByName(city : String) : Unit {
        println("wew")
    }
}