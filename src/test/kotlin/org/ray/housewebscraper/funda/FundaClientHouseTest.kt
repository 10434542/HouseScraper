package org.ray.housewebscraper.funda

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import io.mockk.*
import org.ray.housewebscraper.model.interfaces.HouseWebClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class FundaClientHouseTest {

    @Autowired
    private lateinit var client: WebTestClient;

    @Autowired
    @Qualifier("Funda")
    private lateinit var fundaClient: HouseWebClient;


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
    }

    @Test
    fun getHousesByCityWithinRange() {
        runBlocking { fundaClient.getHousesByCityWithinRange("haarlem",100000L, 300000L)}
    }
}