package org.ray.housewebscraper

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.util.MimeTypeUtils
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient


private const val CITY = """haarlem"""

class ParserTest {

    private val webclient: WebClient = WebClient.builder()
        .exchangeStrategies(ExchangeStrategies.builder().codecs { codecs ->
            codecs.defaultCodecs().maxInMemorySize(1000000)
            codecs.defaultCodecs().jaxb2Decoder(
                getJaxbEncoder()
            )
        }.build())
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient.create().followRedirect(true)
            )
        ).build()


    @Test
    fun testParsing(): Unit = runBlocking {
        val result = webclient.get()
            .uri("https://funda.nl/koop/$CITY/0-350000")
            .accept(MediaType.APPLICATION_XML)
            .retrieve()
            .awaitBody<String>()
//        println(result)
        val document: Document = Jsoup.parse(result)
        val regex = """^/koop/$CITY/[0-9]+-[0-9]+/p[0-9]+/$"""
        val links = document.select("[href~=$regex]").not("[rel]")
        val highestPage = links.map {
            val tempList = it.attr("href").split("/")
            tempList[tempList.size - 2]
        }.last()
        println(highestPage)
        val allLinks = generateSequence(1) { it + 1 }.take(
            highestPage.split("p").last().toInt()
        ).map { "https://funda.nl/koop/$CITY/0-350000/p$it" }
        val results = //                val whatever = webclient.get()
//                    .uri("https://funda.nl/koop/haarlem/0-350000")
//                    .accept(MediaType.APPLICATION_XML)
//                    .retrieve()
//                    .awaitBody<String>()

            allLinks.map {
                async(Dispatchers.IO) {
                    // this is it!
                    println("starting $it on thread ${Thread.currentThread()}" )
                    delay((0..10).random().toLong() * 1000)
                    it
//                val whatever = webclient.get()
//                    .uri("https://funda.nl/koop/haarlem/0-350000")
//                    .accept(MediaType.APPLICATION_XML)
//                    .retrieve()
//                    .awaitBody<String>()

                }
            }.toList()
        val houses = results.awaitAll().map{
            async {

            }
        }
    }

    @Test
    fun whatever() {
        Jaxb2XmlDecoder()
        print("joe")
    }

    private fun acceptedCodecs(clientCodecConfigurer: ClientCodecConfigurer): () -> Unit = {
        clientCodecConfigurer.defaultCodecs().maxInMemorySize(-1)

//        clientCodecConfigurer.customCodecs().register(Jaxb2XmlEncoder())
//        clientCodecConfigurer.customCodecs().register(Jaxb2XmlDecoder(
//            MediaType(TEXT_HTML, StandardCharsets.UTF_8)))
    }

    private fun getJaxbEncoder(): Jaxb2XmlDecoder {
        //        encoder.maxInMemorySize = 1000000
        return Jaxb2XmlDecoder(
            MimeTypeUtils.APPLICATION_XML,
            MimeTypeUtils.TEXT_XML,
            MediaType("application", "*+xml"),
            MimeTypeUtils.TEXT_HTML
        )
    }
}
