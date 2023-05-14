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
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(TriggerController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
internal class BuyHouseControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @MockkBean(relaxed = true)
    private lateinit var service: HouseService

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
    fun `given some buyhouse for Rome, when getByCity, then expect buyHouseDTO`(): Unit = runBlocking {
        coEvery { service.getHousesByCity("Rome") } returns flowOf(buyHouseDTO)
        val result = webClient.get()
            .uri { uri -> uri.path("/api/houses/{city}") }
    }

    @Test
    fun getByAdress() {
    }
}