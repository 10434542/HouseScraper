package org.ray.housewebscraper.persistence.repositories

import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.persistence.BuyHouseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"])
@ExtendWith(SpringExtension::class)
internal class MongoDatabaseContainerTest {
    // DONE : fix authentication issue due to indexOps method in mongoconfiguration of this project.
    // TODO: enable authentication using spring security?
    //    also check https://github.com/testcontainers/testcontainers-java/issues/6420 for the "failed to close response"
    //    message ocurring in the logs
    // TOOD: remove autoconfiguration


    @Autowired
    private lateinit var repository: BuyHouseRepository

    @Autowired
    private lateinit var template: ReactiveMongoTemplate

    private lateinit var document: BuyHouseDocument

    companion object {

        @JvmStatic
        @Container
        private val MONGO_DB_CONTAINER = GenericContainer(
            DockerImageName.parse("mongo:5.0.0")
        )
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "USERNAME")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "PASSWORD")
//            .withEnv("MONGO_INITDB_DATABASE", "TEST_DATABASE")
            .withExposedPorts(27017)
            .waitingFor(Wait.forLogMessage("(?i).*Waiting for connections*.*", 1))

        @JvmStatic
        @DynamicPropertySource
        fun mongoDbProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.username") { "USERNAME" }
            registry.add("spring.data.mongodb.password") { "PASSWORD" }
            registry.add("spring.data.mongodb.host") { MONGO_DB_CONTAINER.host }
            registry.add("spring.data.mongodb.port") { MONGO_DB_CONTAINER.firstMappedPort }
        }

        @BeforeAll
        fun setup() {
            MONGO_DB_CONTAINER.start()
        }

        @AfterAll
        fun cleanup() {
            MONGO_DB_CONTAINER.stop()
        }
    }

    @BeforeEach
    fun setUp() {
        runBlocking {
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
            template.insert(document).awaitSingle()
        }
    }


    @AfterEach
    fun cleanUp() {
        return runBlocking {
            template.remove(
                Query(
                    Criteria
                        .where("_id").`is`(ZipCodeHouseNumber("1010NU", "66"))
                ), "BuyHouses"
            ).block()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `With BuyHouseDocument present, getBuyHouseById will return a document`(): Unit = runTest {
        val results = repository.getBuyHousesByCity("Amsterdam").single()
        Assertions.assertEquals(results, document)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a buyHouseDocument present, when update then getBuyHouseById will return an updated document`(): Unit =
        runTest {
            val mutation = repository.updateHousePriceById("1010NU", "66", "150000")
            val result = repository.getBuyHouseById(ZipCodeHouseNumber("1010NU", "66"))
            assertThat(result?.price).isEqualTo("150000")
        }
}
