package org.ray.housewebscraper.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.stereotype.Service

@Service
class HouseService(
    private val buyHouseRepository: BuyHouseRepository,
    private val buyHouseDTODocumentMapper: BuyHouseDTODocumentMapper,
) {


    suspend fun getHousesByCity(cityName: String): Flow<BuyHouseDTO> {
        return buyHouseRepository.getBuyHousesByCity(cityName).map(buyHouseDTODocumentMapper::toDTO)
    }

    suspend fun getHousesInPriceRange(minimum: Int, maximum: Int): List<BuyHouseDTO> {
        return buyHouseRepository.getBuyHousesInPriceRange(minimum.toString(), maximum.toString())
            .map(buyHouseDTODocumentMapper::toDTO)
    }

    suspend fun getByZipCodeHouseNumber(zipCode: String, houseNumber: String): BuyHouseDTO {
        val buyHouseById = buyHouseRepository.getBuyHouseById(ZipCodeHouseNumber(zipCode, houseNumber))
        return buyHouseDTODocumentMapper.toDTO(buyHouseById)
    }

}