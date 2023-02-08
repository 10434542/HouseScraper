package org.ray.housewebscraper.pararius

import org.ray.housewebscraper.model.entities.BuyHouseDTO
import org.ray.housewebscraper.model.interfaces.HouseWebClient

class ParariusClientHouse : HouseWebClient {
    override suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long): List<BuyHouseDTO> {
        TODO("Not yet implemented")
    }
}