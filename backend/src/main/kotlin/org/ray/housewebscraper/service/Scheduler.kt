package org.ray.housewebscraper.service

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(
    private val scraperService: ScraperService,
) {

    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT5S")
    fun getLatestHousesByCity() {
        runBlocking {
            scraperService.scrapeHousesForCityInRangeAndSave("Haarlem", 0, 300000, 1)
                .onEach { println("found $it") }
                .collect()
        }
    }
}
