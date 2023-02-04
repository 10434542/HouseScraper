package org.ray.housewebscraper.model

interface HouseWebClient {

    suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long)

    suspend fun getBuyHouseByCityAndPriceRange(cityName: String, range: LongRange)
}
