package org.ray.housewebscraper.model.interfaces

import arrow.core.Either
import org.ray.housewebscraper.model.entities.BuyHouseDTO
import org.springframework.web.reactive.function.client.WebClientResponseException

interface HouseWebClient {
    suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long) :  List<BuyHouseDTO>
}
