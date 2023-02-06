package org.ray.housewebscraper.model

interface HouseWebClient {

    suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long) : List<BuyHouseDTO>

    suspend fun getBuyHouseByCityAndPriceRange(cityName: String, range: LongRange)
}
