package org.ray.housewebscraper.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.stereotype.Service

@Service
class HouseService(
    private val buyHouseRepository: BuyHouseRepository
) {


    fun getHousesByCity(cityName: String): Flow<BuyHouseDocument> {
        return runBlocking { buyHouseRepository.getBuyHousesByCity(cityName) }
    }

    fun getHousesInPriceRange(minimum: Int, maximum: Int) {
        return runBlocking { buyHouseRepository}
        TODO("need to first split responsibility of searching and putting houses to guarantee a clean architecture ")
//        return runBlocking { buyHouseRepository }
    }
}