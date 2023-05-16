package org.ray.housewebscraper.funda

import arrow.core.getOrElse
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.apache.http.HttpHeaders
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.HouseWebScraperSpringApplication
import org.ray.housewebscraper.configuration.WireMockContextInitializer
import org.ray.housewebscraper.model.HouseWebClient
import org.ray.housewebscraper.utility.testslices.NoMongoConfigurationPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
    }

    @AfterEach
    fun tearDown() {
        wmServer.resetAll()
    }

    private fun stubResponse(
        url: String,
        responseBody: String,
        responseStatus: Int = HttpStatus.OK.value()
    ) {
        wmServer.stubFor(
            get(url)
                .willReturn(
                    aResponse()
                        .withStatus(responseStatus)
                        .withBody(responseBody)
                        .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getHousesByCityWithinRange(): Unit = runTest {
        stubResponse("localhost:${wmServer.port()}", "empty", HttpStatus.BAD_REQUEST.value())
        val result = fundaClient.getHousesByCityWithinRange("haarlem", 100000L, 300000L, 1)
        assertThat(result.isLeft()).isEqualTo(true)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testResource(): Unit {
        stubResponse("/koop/haarlem/100000-300000",
            getResource("fundaTestResponse.html"), HttpStatus.OK.value())
        stubResponse("/koop/haarlem/100000-300000/p1",
            getResource("fundaTestResponse.html"), HttpStatus.OK.value())
        return runBlocking {
            val result = fundaClient.getHousesByCityWithinRange("haarlem", 100000L, 300000L, 1)
            assertThat(result.isRight()).isEqualTo(true)
            assertThat(result.getOrElse { null }).isNotEmpty()
        }
    }

    @Test
    fun contextLoads() {
    }

    fun getResource(path: String) : String {
        return this.javaClass.classLoader.getResource("webclientresponses/funda/fundaTestResponse.html")?.readText(Charsets.UTF_8) ?: "not found"
    }
}
