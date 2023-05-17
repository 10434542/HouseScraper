package org.ray.housewebscraper.web

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.service.ScraperService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(TriggerController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
internal class TriggerControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient


    @MockkBean(relaxed = true)
    private lateinit var scraper: ScraperService

    private val buyHouseDocument: BuyHouseDocument = BuyHouseDocument(
        ZipCodeHouseNumber("6969TT", "69"),
        "blazeItStreet",
        city = "Rome",
        price = "420",
        surface = "420m2",
        numberOfRooms = "69",
        link = "google.com"
    )

    private val buyHouseDTO: BuyHouseDTO = BuyHouseDTO(
        ZipCodeHouseNumber("6969TT", "69"),
        "blazeItStreet",
        city = "Rome",
        price = "420",
        surface = "420m2",
        numberOfRooms = "69",
        link = "google.com"
    )

    @Test
    fun `given some result from scraperService, when save is disabled then expect houseDtos`(): Unit = runBlocking {
        coEvery { scraper.scrapeHousesForCityInRange("Rome", 0, 1000000, 1) } returns listOf(buyHouseDTO)
        val result = webClient.mutateWith(csrf()).post().uri { uri ->
            uri.path("/api/houses/trigger/buyhouses")
                .queryParam("cityName", "Rome")
                .queryParam("maximum", "100000")
                .queryParam("minimum", "0")
                .queryParam("pages", "1")
                .queryParam("save", "false")
                .build()
        }.exchange()
            .expectStatus().isOk
        println("wew")
    }

    @Test
    fun `given some result from scraperService, whne save is enabledthne expect houseDtos and repository called`(): Unit =
        runBlocking {
            coEvery { scraper.scrapeHousesForCityInRangeAndSave("Rome", 0, 1000000, 1) } returns listOf(buyHouseDTO)

            val result = webClient.mutateWith(csrf()).post().uri { uri ->
                uri.path("/api/houses/trigger/buyhouses")
                    .queryParam("cityName", "Rome")
                    .queryParam("maximum", "100000")
                    .queryParam("minimum", "0")
                    .queryParam("pages", "1")
                    .queryParam("save", "true")
                    .build()
            }.exchange()
                .expectStatus().isOk
        }
}