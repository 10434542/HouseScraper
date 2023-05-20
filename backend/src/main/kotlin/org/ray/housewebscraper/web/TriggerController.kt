package org.ray.housewebscraper.web

import kotlinx.coroutines.flow.Flow
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.service.ScraperService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/houses/trigger")
class TriggerController(
    private val houseScraperService: ScraperService
) {

    @PostMapping("/buyhouses")
    suspend fun scrapeHouses(
        @RequestParam(required = true) cityName: String,
        @RequestParam(required = false) minimum: Long?,
        @RequestParam(required = true) maximum: Long,
        @RequestParam(required = false) pages: Int?,
        @RequestParam(required = false, defaultValue = "false") save: Boolean,
    ): Flow<BuyHouseDTO> {
        if (save) {
            return houseScraperService.scrapeHousesForCityInRangeAndSave(
                cityName,
                minimum ?: 0,
                maximum,
                pages ?: 1

            )
        }
        return houseScraperService.scrapeHousesForCityInRange(
            cityName,
            minimum ?: 0,
            maximum,
            pages ?: 1
        )
    }
}