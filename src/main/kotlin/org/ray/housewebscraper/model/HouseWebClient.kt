package org.ray.housewebscraper.model

import arrow.core.Either
import org.ray.housewebscraper.model.BuyHouseDTO

interface HouseWebClient {
    suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long, pages: Int) :  Either<Throwable, List<BuyHouseDTO>>
}
