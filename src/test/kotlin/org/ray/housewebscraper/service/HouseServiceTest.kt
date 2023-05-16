package org.ray.housewebscraper.service

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ray.housewebscraper.mapper.BuyHouseDTODocumentMapper
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.persistence.BuyHouseRepository

internal class HouseServiceTest {
    private val buyHouseDTODocumentMapper = mockk<BuyHouseDTODocumentMapper>(relaxed = true)
    private val buyHouseRepository = mockk<BuyHouseRepository>(relaxed = true)
    private val houseService = HouseService(buyHouseRepository, buyHouseDTODocumentMapper)
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
    fun `getHousesByZipCodeHouseNumber will return a houseDto if available`(): Unit = runBlocking {
        coEvery { buyHouseRepository.getBuyHouseById(any()) } returns buyHouseDocument
        every { buyHouseDTODocumentMapper.toDTO(buyHouseDocument) } returns buyHouseDTO
        val result = houseService.getByZipCodeHouseNumber("6969TT", "69")
        assertThat(result).isEqualTo(buyHouseDTO)
    }

    @Test
    fun `getHouseByZipCodeHouseNumber will return null if house is not found`(): Unit = runBlocking {
        coEvery { buyHouseRepository.getBuyHouseById(any()) } throws(NoSuchElementException())
        assertThrows<NoSuchElementException> {
            houseService.getByZipCodeHouseNumber(
                "6969TT",
                "69"
            )
        }

    }

    @Test
    fun `getHousesInPriceRange will return a list containing buyHouseDTO if the document is available`(): Unit = runBlocking {
        coEvery { buyHouseRepository.getBuyHousesInPriceRange("0", "200000") } returns listOf(buyHouseDocument)
        every { buyHouseDTODocumentMapper.toDTO(buyHouseDocument) } returns buyHouseDTO
        val result = houseService.getHousesInPriceRange(0, 200000)
        assertThat(result).contains(buyHouseDTO)
    }

    @Test
    fun `getHousesByCity will return a flow containing buyHouseDTO if the document is available`(): Unit = runBlocking {
        coEvery { buyHouseRepository.getBuyHousesByCity("Rome") } returns flowOf(buyHouseDocument)
        every { buyHouseDTODocumentMapper.toDTO(buyHouseDocument) } returns buyHouseDTO
        val result = houseService.getHousesByCity("Rome")
        assertThat(result.toList(mutableListOf())).contains(buyHouseDTO)
    }
}