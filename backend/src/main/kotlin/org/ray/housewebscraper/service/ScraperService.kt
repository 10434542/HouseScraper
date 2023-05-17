package org.ray.housewebscraper.service

import arrow.core.flatten
import arrow.core.flattenOption
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseWebClient
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.stereotype.Service

@Service
class ScraperService(
    private val webClients: List<HouseWebClient>,
    private val buyHouseDTODocumentMapper: BuyHouseDTODocumentMapper,
    private val buyHouseRepository: BuyHouseRepository
) {
    suspend fun scrapeHousesForCityInRange(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int
    ): List<BuyHouseDTO> {
        val result = coroutineScope {
            webClients.map {
                async {
                    it.getHousesByCityWithinRange(cityName, minimum, maximum, pages)
                }
            }
        }.awaitAll().map {
            it.getOrNone()
        }.filter {
            it.nonEmpty()
        }.flattenOption()
            .flatten()
        return result
    }

    suspend fun scrapeHousesForCityInRangeAndSave(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int
    ): List<BuyHouseDTO> {
        val scrapedHouses = scrapeHousesForCityInRange(cityName, minimum, maximum, pages)
        buyHouseRepository.insertAll(scrapedHouses.map(buyHouseDTODocumentMapper::toDocument))
        return scrapedHouses
    }
}