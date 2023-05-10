package org.ray.housewebscraper.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.stereotype.Service

@Service
class HouseService(
    private val buyHouseRepository: BuyHouseRepository,
    private val buyHouseDTODocumentMapper: BuyHouseDTODocumentMapper
) {


    fun getHousesByCity(cityName: String): Flow<BuyHouseDocument> {
        return runBlocking { buyHouseRepository.getBuyHousesByCity(cityName) }
    }

    fun getHousesInPriceRange(minimum: Int, maximum: Int) : List<BuyHouseDTO> {
        return runBlocking { buyHouseRepository.getBuyHousesInPriceRange(minimum.toString(), maximum.toString()).map(buyHouseDTODocumentMapper::toDTO)}
    }
}