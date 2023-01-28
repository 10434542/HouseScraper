package org.ray.housewebscraper

import org.ray.housewebscraper.model.BuyHouse
import org.ray.housewebscraper.model.HouseWebClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api")
class RootController(val clientList: Map<String, HouseWebClient>) {


    @GetMapping("/trigger")
    suspend fun findBuyHouse(): List<BuyHouse> {
        clientList["fundaClientHouse"]?.getBuyHouseByCity("Haarlem")
        return emptyList()
    }

}
