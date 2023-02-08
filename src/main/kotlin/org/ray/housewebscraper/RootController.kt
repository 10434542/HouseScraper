package org.ray.housewebscraper

import org.ray.housewebscraper.model.entities.BuyHouseDTO
import org.ray.housewebscraper.model.interfaces.HouseWebClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api")
class RootController(val clientList: Map<String, HouseWebClient>) {

    @GetMapping("/trigger")
    suspend fun findBuyHouse(): List<BuyHouseDTO> {
        clientList["fundaClientHouse"]?.getHousesByCityWithinRange("Haarlem", 0, 100000)
        return emptyList()
    }

}
