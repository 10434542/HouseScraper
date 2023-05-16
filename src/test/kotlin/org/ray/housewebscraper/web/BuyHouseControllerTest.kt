package org.ray.housewebscraper.web

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.service.HouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(BuyHouseController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
internal class BuyHouseControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @MockkBean(relaxed = true)
    private lateinit var service: HouseService

    private val buyHouseDTO: BuyHouseDTO = BuyHouseDTO(
        ZipCodeHouseNumber("6969TT", "69"),
        "suk_a_kok_street",
        city = "OldMan",
        price = "69",
        surface = "69m2",
        numberOfRooms = "69",
        link = "no_money.com"
    )

    @Test
    fun `given some buyhouse for OldMan, when getByCity, then expect buyHouseDTO`(): Unit = runBlocking {
        coEvery { service.getHousesByCity("OldMan") } returns flowOf(buyHouseDTO)
        val result = webClient.mutateWith(csrf()).get()
            .uri { uri ->
                uri.path("/api/houses/buyhouses/{city}").build("OldMan")
            }.exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `given some buyhouse for some zip code and housenumber, when getByAdress, then expect buyHouseDTO`(): Unit =
        runBlocking {
            coEvery { service.getByZipCodeHouseNumber("6969TT", "69") } returns buyHouseDTO
            val result = webClient.get()
                .uri { uri ->
                    uri.path("/api/houses/buyhouses")
                        .queryParam("zipCode", "6969TT")
                        .queryParam("houseNumber", "69")
                        .build()
                }.exchange()
                .expectStatus()
                .isOk
        }
}