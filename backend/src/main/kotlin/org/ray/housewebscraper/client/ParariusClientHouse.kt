package org.ray.housewebscraper.client

import arrow.core.Either
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseWebClient

class ParariusClientHouse : HouseWebClient {
    override suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long, pages: Int):
            Either<Throwable, List<BuyHouseDTO>> {
        TODO("Not yet implemented")
    }
}