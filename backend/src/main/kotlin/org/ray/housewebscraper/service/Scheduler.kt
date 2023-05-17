package org.ray.housewebscraper.service

import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(
    private val buyHouseRepository: BuyHouseRepository,
    private val buyHouseDTODocumentMapper: BuyHouseDTODocumentMapper,
    private val scraperService: ScraperService,
) {

    @Scheduled(fixedDelayString = "PT15M", initialDelayString = "PT5S")
    suspend fun getLatestHousesByCity() {
        val result = scraperService.scrapeHousesForCityInRange("Haarlem", 0, 300000, 1)
        buyHouseRepository.insertAll(result.map(buyHouseDTODocumentMapper::toDocument))
    }
}
