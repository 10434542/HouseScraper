package org.ray.housewebscraper.persistance.repositories

import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.ray.housewebscraper.model.entities.BuyHouseDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@ComponentScan
@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class MongoDatabaseContainerTest {
    // DONE : fix authentication issue due to indexOps method in mongoconfiguration of this project.
    // TODO: enable authentication using spring security?
    //    also check https://github.com/testcontainers/testcontainers-java/issues/6420 for the "failed to close response"
    //    message ocurring in the logs

    @Autowired
    private lateinit var repository: BuyHouseRepository

    @Autowired
    private lateinit var template: ReactiveMongoTemplate

    private lateinit var document: BuyHouseDocument

    companion object {
        private const val BUY_HOUSE_COLLECTION = "BuyHouses"

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

    @BeforeEach
    fun setUp() {
        mockkStatic("kotlinx.coroutines.reactor.MonoKt")
        MONGO_DB_CONTAINER.execInContainer("db.${BUY_HOUSE_COLLECTION}.deleteMany({})")
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
            Assertions.assertEquals(result, document)
        }
    }

    @Test
    fun `given a buyHouseDocument present, when update then getBuyHouseById will return an updated document`() {
        runBlocking {
            TODO("write test for the update method of buyHouseRepositoryImpl")
        }
    }
}