package org.ray.housewebscraper.persistance.repositories

import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.test.web.reactive.server.WebTestClient

class BuyHouseRepositoryImplTest {
    val responseSpec = mockk<WebTestClient.ResponseSpec>(relaxed = true) // fixed it
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun insert() {
    }

    @Test
    fun getBuyHouseById() {
    }

    @Test
    fun getBuyHousesByCity() {
    }
}