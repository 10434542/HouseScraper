package org.ray.housewebscraper.model

import arrow.core.Either

interface HouseWebClient {
    suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long, pages: Int) :  Either<Throwable, List<BuyHouseDTO>>
}
