package org.ray.housewebscraper.funda

import com.github.tomakehurst.wiremock.WireMockServer
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.ray.housewebscraper.HouseWebScraperSpringApplication
import org.ray.housewebscraper.configuration.WireMockContextInitializer
import org.ray.housewebscraper.model.interfaces.HouseWebClient
import org.ray.housewebscraper.utility.testslices.NoMongoConfigurationPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(
    classes = [HouseWebScraperSpringApplication::class],
    initializers = [WireMockContextInitializer::class]
)
internal class FundaClientHouseTest : NoMongoConfigurationPresent() {

    @Autowired
    private lateinit var wmServer: WireMockServer

    @Autowired
    @Qualifier("Funda")
    private lateinit var fundaClient: HouseWebClient


    @BeforeEach
    fun setUp() {
//        `when`(
//            client.get().uri(anyString()).accept().retrieve()
//                .tryAwaitBodyOrElseEither<String>()
//        ).thenReturn(this::class.java.getResource("firstAnswer.html")
//            ?.let { Either.Right(it.readText(Charsets.UTF_8)) })
//            .thenReturn(this::class.java.getResource("secondAnswer.html")
//                ?.let { Either.Right(it.readText(Charsets.UTF_8)) })
    }

    @AfterEach
    fun tearDown() {
        wmServer.resetAll()
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun getHousesByCityWithinRange(): Unit = runTest {
//        val result = fundaClient.getHousesByCityWithinRange("haarlem", 100000L, 300000L, 1)
//        assertThat(result.isLeft()).isEqualTo(true)
//    }

    @Test
    fun contextLoads() {
    }
}