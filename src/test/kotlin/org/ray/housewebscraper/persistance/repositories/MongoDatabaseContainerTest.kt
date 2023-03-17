package org.ray.housewebscraper.persistance.repositories

import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration
import io.mockk.mockkStatic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.ray.housewebscraper.model.entities.ZipCodeHouseNumber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@ComponentScan(excludeFilters = [ComponentScan.Filter(EmbeddedMongoAutoConfiguration::class)])
@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
        private const val BUY_HOUSE_COLLECTION = "BuyHouses"
        private val mongo = MongoDBContainer("mongo:5.0.0")

        @OptIn(ExperimentalCoroutinesApi::class)
        private val testDispatcher = StandardTestDispatcher()

        @JvmStatic
        @Container
        private val MONGO_DB_CONTAINER = GenericContainer(
            DockerImageName.parse("mongo:5.0.0")
        )
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "USERNAME")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "PASSWORD")
            .withEnv("MONGO_INITDB_DATABASE", "TEST_DATABASE")
            .withExposedPorts(27017)
            .waitingFor(Wait.forLogMessage("(?i).*Waiting for connections*.*", 1))

        @JvmStatic
        @DynamicPropertySource
        fun mongoDbProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.username") { "USERNAME" }
//            registry.add("spring.data.mongodb.uri") { MONGO_DB_CONTAINER.replicaSetUrl }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp(): Unit = synchronized(this) {
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
        synchronized(this) {
            runBlocking {
                var insert = withContext(Dispatchers.IO) {
                    template.insert(document).block()
                }
            }
        }
    }

    @AfterEach
    fun cleanUp() {
        runBlocking {

            launch {template.remove(
                Query(
                    Criteria
                        .where("zipCodeHouseNumber").`is`(ZipCodeHouseNumber("1010NU", "66"))
                ), "BuyHouses"
            )}.join()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `With BuyHouseDocument present, getBuyHouseById will return a document`(): Unit = synchronized(this) {
        runTest {
            val results = repository.getBuyHousesByCity("Amsterdam").single()
            Assertions.assertEquals(results, document)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a buyHouseDocument present, when update then getBuyHouseById will return an updated document`(): Unit =
        synchronized(this) {
            runTest() {
                val mutation = repository.updateHousePriceById("1010NU", "66", "150000")
                val result = repository.getBuyHouseById(ZipCodeHouseNumber("1010NU", "66"))
                assertThat(result.price).isEqualTo("150000")
            }
        }
}
