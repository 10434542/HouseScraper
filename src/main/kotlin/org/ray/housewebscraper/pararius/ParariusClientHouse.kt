package org.ray.housewebscraper.pararius

import arrow.core.Either
import org.ray.housewebscraper.model.entities.BuyHouseDTO
import org.ray.housewebscraper.model.interfaces.HouseWebClient

class ParariusClientHouse : HouseWebClient {
    override suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long, pages: Int):
            Either<Throwable, List<BuyHouseDTO>> {
        TODO("Not yet implemented")
    }
}