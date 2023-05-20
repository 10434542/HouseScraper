package org.ray.housewebscraper.service

import arrow.core.Either
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseWebClient
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.persistence.BuyHouseRepository


class ScraperServiceTest {
    private val mockClient: HouseWebClient = mockk<HouseWebClient>(relaxed = true)
    private val mapper: BuyHouseDTODocumentMapper = mockk<BuyHouseDTODocumentMapper>(relaxed = true)
    private val repository: BuyHouseRepository = mockk<BuyHouseRepository>(relaxed = true)
    private val buyHouseDTO: BuyHouseDTO = BuyHouseDTO(
        ZipCodeHouseNumber("6969TT", "69"),
        "blazeItStreet",
        city = "Rome",
        price = "420",
        surface = "420m2",
        numberOfRooms = "69",
        link = "google.com"
    )
    private val buyHouseDocument: BuyHouseDocument = BuyHouseDocument(
        ZipCodeHouseNumber("6969TT", "69"),
        "blazeItStreet",
        city = "Rome",
        price = "420",
        surface = "420m2",
        numberOfRooms = "69",
        link = "google.com"
    )

    private val service: ScraperService = ScraperService(
        listOf(mockClient),
        mapper,
        repository,
    )

    @Test
    fun `given right Either, when scrapeHousesForCityInRange then expect buyHouseDTO`(): Unit = runBlocking {
        coEvery { mockClient.getHousesByCityWithinRange("Rome", 0, 1000000, 1) } returns Either.catch {
            listOf(
                buyHouseDTO
            )
        }
        val result = service.scrapeHousesForCityInRange("Rome", 0, 1000000, 1).toList()
        assertThat(result).contains(buyHouseDTO)
    }

    @Test
    fun `given right Either, when scrapeHousesForCityInRangeAndSave, then expect buyHouseDTO `(): Unit = runBlocking {
        coEvery { mockClient.getHousesByCityWithinRange("Rome", 0, 1000000, 1) } returns Either.catch {
            listOf(
                buyHouseDTO
            )
        }
        every { repository.insertAll(listOf(buyHouseDocument)) } returns flowOf(buyHouseDocument)
        every { mapper.toDocument(buyHouseDTO) } returns buyHouseDocument
        every { mapper.toDTO(buyHouseDocument) } returns buyHouseDTO
        val result = service.scrapeHousesForCityInRangeAndSave("Rome", 0, 1000000, 1).toList()
        coVerify { repository.insertAll(listOf(buyHouseDocument)) }
        assertThat(result).contains(buyHouseDTO)
    }
}