package org.ray.housewebscraper.model.interfaces

import arrow.core.Either
import org.ray.housewebscraper.model.entities.BuyHouseDTO

interface HouseWebClient {
    suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long) :  Either<Throwable, List<BuyHouseDTO>>
}
