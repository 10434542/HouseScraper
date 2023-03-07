package org.ray.housewebscraper.funda

import arrow.core.Either
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.mockito.Mockito.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.reactivestreams.Publisher
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.http.HttpConnectTimeoutException
import kotlin.reflect.KClass

internal class FundaClientHouseTest {



    private val mockResponse = mockk<WebClient.ResponseSpec>(relaxed = true)


    private var fundaClient: FundaClientHouse = FundaClientHouse(getWebClientMock("test"))


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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getHousesByCityWithinRange(): Unit = runTest {
        mockkStatic("kotlinx.coroutines.reactive.AwaitKt")
        mockkStatic("org.ray.housewebscraper.funda.ZipKt")
        mockkStatic(Publisher<String>::awaitSingle)
        val mock = mockk<WebClient>(relaxed = true)
        val specMock = mockk<WebClient.ResponseSpec>(relaxed = true)
        val publisherMock = mockk<Publisher<String>>(relaxed = true)
        val uriSpecMock = mockk<WebClient.RequestHeadersUriSpec<*>>(relaxed = true)
        val headersSpecMock = mockk<WebClient.RequestHeadersSpec<*>>(relaxed = true)
        val responseSpecMock = mockk<WebClient.ResponseSpec>(relaxed = true)
        coEvery {
            mock.get().accept(isNull(inverse = true)).retrieve().tryAwaitBodyOrElseEither<String>()
        } returns Either.Left(HttpConnectTimeoutException("oopsie")) coAndThen {
            Either.Left(HttpConnectTimeoutException("oopsie"))
        } coAndThen { Either.Left(HttpConnectTimeoutException("oopsie")) }
//        coEvery { uriSpecMock.uri(any<String>()) } returns headersSpecMock
//        coEvery { headersSpecMock.header(isNull(inverse = true), isNull(inverse = true)) } returns headersSpecMock
//        coEvery { headersSpecMock.headers(isNull(inverse = true)) } returns headersSpecMock
//        coEvery { headersSpecMock.accept(isNull(inverse = true)) } returns headersSpecMock
//        coEvery { headersSpecMock.retrieve() } returns responseSpecMock
//        coEvery { responseSpecMock.bodyToMono(String::class.java) } returns Mono.just("just")
//        coEvery { specMock.awaitBody<String>()} returns {"whatever"}.toString()
        coEvery { publisherMock.awaitSingle() } returns "TestData"
//        mockkStatic("org.ray.housewebscraper.funda.FundaClientHouse.kt")
//        val faultyEither: Either<Throwable, String> = Either.Left(HttpConnectTimeoutException("Timed out"))
//        coEvery { specMock.tryAwaitBodyOrElseEither<String>() } returns faultyEither

//        coEvery {
//            mockResponse.tryAwaitBodyOrElseEither<String>()
//        } returns faultyEither coAndThen {
//            faultyEither
//        }
        val result = fundaClient.getHousesByCityWithinRange("haarlem", 100000L, 300000L, 1)

        assertThat(result.isLeft()).isEqualTo(true)
    }

    @Test
    fun contextLoads() {
    }


    private fun getWebClientMock(resp: String): WebClient {
        val mock = mockk<WebClient>(relaxed = true)
        val uriSpecMock = mockk<WebClient.RequestHeadersUriSpec<*>>(relaxed = true)
        val headersSpecMock = mockk<WebClient.RequestHeadersSpec<*>>(relaxed = true)
        val responseSpecMock = mockk<WebClient.ResponseSpec>(relaxed = true)

        every { mock.get() } returns uriSpecMock
        every { uriSpecMock.uri(any<String>()) } returns headersSpecMock
        every { headersSpecMock.header(isNull(inverse = true), isNull(inverse = true)) } returns headersSpecMock
        every { headersSpecMock.headers(isNull(inverse = true)) } returns headersSpecMock
        every { headersSpecMock.accept(isNull(inverse = true)) } returns headersSpecMock
        every { headersSpecMock.retrieve() } returns responseSpecMock
        every { responseSpecMock.bodyToMono(String::class.java) } returns Mono.just(resp)
        return mock

    }
}