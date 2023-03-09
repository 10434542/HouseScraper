package org.ray.housewebscraper.persistance.repositories

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @BeforeEach
    fun setUp() {

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert(): Unit = runTest {
        val mono = Mono.just(document)
        every { mockTemplate.insert(any<BuyHouseDocument>(), any()) } returns mono
        coEvery { mono.awaitSingle() } returns document
//        coEvery { mockTemplate.insert(any<BuyHouseDocument>()).awaitSingle() } returns document
        val result = repository.getBuyHouseById("1010NU66")
        assertThat(result).isEqualTo(document)
    }

    @Test
    fun getBuyHouseById() {
    }

    @Test
    fun getBuyHousesByCity() {
    }
    @Test
    fun testMongoTemplate() = runBlocking {
        mockkStatic("kotlinx.coroutines.reactor.MonoKt")
//        val mockMongoTemplate = mockk<ReactiveMongoTemplate>(relaxed = true)
//        val mockResult = mockk<BuyHouseDocument>()
        val mockInsertResult = mockk<BuyHouseDocument>(relaxed = true)

        // mock the insert operation to return a Mono of MyModel
        every { mockTemplate.insert(any<BuyHouseDocument>()) } returns Mono.just(mockInsertResult)

        // mock the awaitSingle function to return the mockResult
        coEvery { any<Mono<BuyHouseDocument>>().awaitSingle() } returns document

        // call the code under test
//        val result = someFunctionThatUsesMongoTemplate(mockTemplate, mockResult)
        val result = repository.getBuyHouseById("1010NU66")

        // assert that the result matches the mock result
        assertEquals(document, result)
        // verify that the insert operation was called with the correct argument
    }
}