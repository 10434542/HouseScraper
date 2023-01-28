package org.ray.housewebscraper.model

interface HouseWebClient {

    suspend fun getBuyHouseByCity(cityName: String): BuyHouse

    suspend fun getBuyHouseByCityAndPriceRange(cityName: String, range: LongRange)
}
