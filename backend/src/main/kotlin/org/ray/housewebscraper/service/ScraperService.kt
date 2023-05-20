package org.ray.housewebscraper.service

import arrow.core.flatten
import arrow.core.flattenOption
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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
    fun scrapeHousesForCityInRange(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int
    ): Flow<BuyHouseDTO> {
        val result: Flow<BuyHouseDTO> = flow {
            coroutineScope {
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
                .flatten().forEach {
                    emit(it)
                }
        }
        return result
    }

    fun scrapeHousesForCityInRangeAndSave(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int
    ): Flow<BuyHouseDTO> {
        val tempHouses = runBlocking { scrapeHousesForCityInRange(cityName, minimum, maximum, pages).toList() }
        return buyHouseRepository.insertAll(tempHouses.map { buyHouseDTODocumentMapper.toDocument(it) })
            .map { buyHouseDTODocumentMapper.toDTO(it) }
    }
}