package org.ray.housewebscraper.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.ray.housewebscraper.model.HouseWebClient
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(
    private val webClients: List<HouseWebClient>,
    private val buyHouseRepository: BuyHouseRepository
) {

    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT5S")
    suspend fun getLatestHousesByCity() {
        val result = coroutineScope {
            webClients.map {
                async {
                    it.getHousesByCityWithinRange("Haarlem", 0, 300000, 1)
                }
            }
        }.awaitAll()
    }
}
