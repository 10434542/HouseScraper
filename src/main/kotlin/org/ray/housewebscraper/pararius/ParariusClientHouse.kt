package org.ray.housewebscraper.pararius

import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseWebClient

class ParariusClientHouse : HouseWebClient {
    override suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long): List<BuyHouseDTO> {
        TODO("Not yet implemented")
    }
}