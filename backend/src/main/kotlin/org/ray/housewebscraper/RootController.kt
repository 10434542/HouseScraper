package org.ray.housewebscraper

import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseWebClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RootController(val clientList: Map<String, HouseWebClient>) {

    @GetMapping("/trigger")
    suspend fun findBuyHouse(): List<BuyHouseDTO> {
        clientList["fundaClientHouse"]?.getHousesByCityWithinRange("Haarlem", 0, 100000, 0)
        return emptyList()
    }

}
