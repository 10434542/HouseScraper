package org.ray.housewebscraper.funda

import org.ray.housewebscraper.model.BuyHouse
import org.ray.housewebscraper.model.HouseWebClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class FundaClientHouse(val webClient: WebClient) : HouseWebClient{
    suspend fun getBuyHouseByName(city : String) : Unit {
        println("wew")
    }

    override suspend fun getBuyHouseByCity(cityName: String): BuyHouse {
        val response = webClient.get()
            .uri("/koop/$cityName/0-350000/")
            .accept(MediaType.APPLICATION_XML)
            .retrieve()
            .awaitBody<String>()
        TODO("Not yet implemented")
    }

    override suspend fun getBuyHouseByCityAndPriceRange(cityName: String, range: LongRange) {
        TODO("Not yet implemented")
    }
}
