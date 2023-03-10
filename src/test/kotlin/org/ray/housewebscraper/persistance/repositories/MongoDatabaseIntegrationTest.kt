package org.ray.housewebscraper.persistance.repositories

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.ray.housewebscraper.HouseWebScraperSpringApplication
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.ray.housewebscraper.utility.testslices.MongoConfigurationPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataMongoTest
@ExtendWith(SpringExtension::class)
@ComponentScan
@ContextConfiguration(classes = [HouseWebScraperSpringApplication::class])
@TestPropertySource(properties = ["spring.mongodb.embedded.version=4.0.12", "spring.mongodb."])
internal class MongoDatabaseIntegrationTest {

    @Autowired
    private lateinit var template: ReactiveMongoTemplate

    @Autowired
    private lateinit var repository: BuyHouseRepository

    private lateinit var document: BuyHouseDocument

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

    @Test
    fun `With BuyHouseDocument present, getBuyHouseById will return a document`() {
        runBlocking {
            var insert = template.insert(document).block()
            val result = repository.getBuyHousesByCity("Amsterdam").first()
            assertEquals(result, document)
        }
    }
}