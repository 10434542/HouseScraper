package org.ray.housewebscraper.funda

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.apache.http.HttpHeaders
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.HouseWebScraperSpringApplication
import org.ray.housewebscraper.configuration.WireMockContextInitializer
import org.ray.housewebscraper.model.interfaces.HouseWebClient
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
        responseStatus: Int = org.springframework.http.HttpStatus.OK.value()
    ) {
        wmServer.stubFor(
            get(url)
                .willReturn(
                    aResponse()
                        .withStatus(responseStatus)
                        .withBody(responseBody)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
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

    @Test
    fun contextLoads() {
    }
}
