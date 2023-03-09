package org.ray.housewebscraper.persistance.repositories

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.core.publisher.Mono

class BuyHouseRepositoryImplTest {
    private val mockTemplate = mockk<ReactiveMongoTemplate>(relaxed = true) // fixed it

    private lateinit var document: BuyHouseDocument;

    private val repository: BuyHouseRepository = BuyHouseRepositoryImpl(mockTemplate);

    private val mockBuyHouseDocument = mockk<BuyHouseDocument>(relaxed = true)


    @BeforeEach
    fun setUp() {
        mockkStatic("kotlinx.coroutines.reactor.MonoKt")

        document = BuyHouseDocument(
            "hank street",
            "66",
            "1010NU",
            "Amsterdam",
            "100000",
            "10m2",
            "1",
            "localhost"
            )

    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getBuyHouseById() {
    }

    @Test
    fun getBuyHousesByCity() {
    }
    @Test
    fun `insert will return a buyHouseDocument`() = runBlocking {
//        val mockMongoTemplate = mockk<ReactiveMongoTemplate>(relaxed = true)
//        val mockResult = mockk<BuyHouseDocument>()
        // mock the insert operation to return a Mono of MyModel
        every { mockTemplate.insert(any<BuyHouseDocument>()) } returns Mono.just(mockBuyHouseDocument)

        // mock the awaitSingle function to return the mockResult
        coEvery { any<Mono<BuyHouseDocument>>().awaitSingle() } returns document

        // call the code under test
//        val result = someFunctionThatUsesMongoTemplate(mockTemplate, mockResult)
        val result = repository.getBuyHouseById("1010NU66")

        // assert that the result matches the mock result
        assertEquals(document, result)
        // verify that the insert operation was called with the correct argument
    }

    @Test
    fun `getHouseById will return a buyHouseDocument`() = runBlocking {

        every { mockTemplate.findOne(any(), any<Class<*>>())} returns Mono.just(mockBuyHouseDocument)
        coEvery { any<Mono<BuyHouseDocument>>().awaitSingle()} returns document

        val result = repository.getBuyHouseById("whatever")
        assertEquals(document, result)
    }

    @Test
    fun `getHouseByCity will return a buyHouseDocument`() = runBlocking {
        mockkStatic("kotlinx.coroutines.reactive.ReactiveFlowKt")

        every { mockTemplate.find(any(), any<Class<*>>()).asFlow()} returns flowOf(document)

        val result = repository.getBuyHousesByCity("Amsterdam")
        assertEquals(document, result.first())

    }
}