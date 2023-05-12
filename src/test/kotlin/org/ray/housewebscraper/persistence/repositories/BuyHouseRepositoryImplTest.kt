package org.ray.housewebscraper.persistence.repositories

import com.mongodb.client.result.UpdateResult
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.bson.BsonDocument
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.ray.housewebscraper.persistence.BuyHouseRepositoryImpl
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.core.publisher.Mono

class BuyHouseRepositoryImplTest {
    private val mockTemplate = mockk<ReactiveMongoTemplate>(relaxed = true) // fixed it

    private lateinit var document: BuyHouseDocument

    private val repository: BuyHouseRepository = BuyHouseRepositoryImpl(mockTemplate)

    private val mockBuyHouseDocument = mockk<BuyHouseDocument>(relaxed = true)


    @BeforeEach
    fun setUp() {
        mockkStatic("kotlinx.coroutines.reactor.MonoKt")

        document = BuyHouseDocument(
            ZipCodeHouseNumber("1010NU", "66"),
            "hank street",
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
    fun `insert will return a buyHouseDocument`() = runBlocking {
//        val mockMongoTemplate = mockk<ReactiveMongoTemplate>(relaxed = true)
//        val mockResult = mockk<BuyHouseDocument>()
        // mock the insert operation to return a Mono of MyModel
        every { mockTemplate.insert(any<BuyHouseDocument>()) } returns Mono.just(mockBuyHouseDocument)

        // mock the awaitSingle function to return the mockResult
        coEvery { any<Mono<BuyHouseDocument>>().awaitSingle() } returns document

        // call the code under test
//        val result = someFunctionThatUsesMongoTemplate(mockTemplate, mockResult)
        val result = repository.getBuyHouseById(ZipCodeHouseNumber("1010NU", "66"))

        // assert that the result matches the mock result
        assertEquals(document, result)
        // verify that the insert operation was called with the correct argument
    }

    @Test
    fun `getHouseById will return a buyHouseDocument`() = runBlocking {

        every { mockTemplate.findOne(any(), any<Class<*>>()) } returns Mono.just(mockBuyHouseDocument)
        coEvery { any<Mono<BuyHouseDocument>>().awaitSingle() } returns document

        val result = repository.getBuyHouseById(ZipCodeHouseNumber("1010NU", "66"))
        assertEquals(document, result)
    }

    @Test
    fun `getHouseByCity will return a buyHouseDocument`() = runBlocking {
        mockkStatic("kotlinx.coroutines.reactive.ReactiveFlowKt")

        every { mockTemplate.find(any(), any<Class<*>>()).asFlow() } returns flowOf(document)

        val result = repository.getBuyHousesByCity("Amsterdam")
        assertEquals(document, result.first())

    }

    @Test
    fun `given a buyHouseDocument present, when update then getBuyHouseById will return an updated document`() {

        runBlocking {
            val updateResult = UpdateResult.acknowledged(1, 2, BsonDocument(1))
            every { mockTemplate.updateFirst(any(), any(), any<Class<*>>()) } returns Mono.just(updateResult)
            coEvery { any<Mono<UpdateResult>>().awaitSingle() } returns updateResult

            val result = repository.updateHousePriceById("1010NU", "66", "1000000");
            assertEquals(updateResult, result)
        }
    }

    @Test
    fun `given a list of buyHouseDocuments, when save all then expect a flow containing those documents`(): Unit =
        runBlocking {
            coEvery { val buyHouseDocuments = mutableListOf(mockBuyHouseDocument)
                mockTemplate.insertAll(buyHouseDocuments).asFlow() } returns flowOf(mockBuyHouseDocument)
            val result = repository.insertAll(listOf(mockBuyHouseDocument)).toList()
            assertThat(result).contains(mockBuyHouseDocument)
        }
}
