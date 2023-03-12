package org.ray.housewebscraper.persistance.repositories

import de.flapdoodle.os.CommonOS
import de.flapdoodle.os.Platform
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.ray.housewebscraper.utility.testslices.MongoConfigurationPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@ComponentScan
@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class MongoDatabaseContainerTest {
    // TODO: fix authentication issue due to indexOps method in mongoconfiguration of this project.

    @Autowired
    private lateinit var repository: BuyHouseRepository

    @Autowired
    private lateinit var template: ReactiveMongoTemplate

    private lateinit var document: BuyHouseDocument

    companion object {

        @JvmStatic
        @Container
        private val MONGO_DB_CONTAINER: MongoDBContainer = MongoDBContainer(
            DockerImageName.parse("mongo:5.0.0"))

        @JvmStatic
        @DynamicPropertySource
        fun mongoDbProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") { MONGO_DB_CONTAINER.replicaSetUrl }
            registry.add("spring.data.mongodb.host") { MONGO_DB_CONTAINER.host }
            registry.add("spring.data.mongodb.port") { 0 }
        }

        @BeforeAll
        fun setup() {
            MONGO_DB_CONTAINER.start();
        }

        @AfterAll
        fun cleanup() {
            MONGO_DB_CONTAINER.stop()
        }
    }

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


    @Test
    fun `With BuyHouseDocument present, getBuyHouseById will return a document`() {
        runBlocking {
            val whatever: Platform = Platform.detect(CommonOS.list())
            var insert = template.insert(document).block()
            val result = repository.getBuyHousesByCity("Amsterdam").first()
            Assertions.assertEquals(result, document)
        }
    }
}