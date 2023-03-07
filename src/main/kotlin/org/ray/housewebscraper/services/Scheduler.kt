package org.ray.housewebscraper.services

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.ray.housewebscraper.model.interfaces.HouseWebClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(@Autowired val webClients: List<HouseWebClient>) {

    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT5S")
    suspend fun getLatestHousesByCity() {
        val result = webClients.map {
           coroutineScope {
               async {
                   it.getHousesByCityWithinRange("Haarlem", 0, 300000, 1)
               }
           }
        }.awaitAll()
    }
}
