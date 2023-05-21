package org.ray.housewebscraper.service

import arrow.core.flatten
import arrow.core.flattenOption
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseWebClient
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

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
                .flatten()
                .forEach {
                    emit(it)
                }
        }.onEach {
            logger.info { "found buyHouseDocument $it" }
        }
        return result
    }

    fun scrapeHousesForCityInRangeAndSave(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int
    ): Flow<BuyHouseDTO> {
        val tempHouses = runBlocking {
            scrapeHousesForCityInRange(cityName, minimum, maximum, pages)
                .map {
                    buyHouseDTODocumentMapper.toDocument(it)
                }.toList()
        }
        return buyHouseRepository.insertAll(tempHouses)
            .map {
                buyHouseDTODocumentMapper.toDTO(it)
            }.onEach {
                logger.info {
                    "saved buyhousedocument $it"
                }
            }
    }
}