package org.ray.housewebscraper

import arrow.core.Either
import com.nimbusds.oauth2.sdk.util.StringUtils.isAlpha
import com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.NodeTraversor
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.entities.BuyHouseDTO
import org.ray.housewebscraper.model.entities.ZipCodeHouseNumber
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.util.MimeTypeUtils
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import reactor.netty.http.client.HttpClient


private const val CITY = """amsterdam"""

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
        val prices = arrayOf(
            50000,
            75000,
            100000,
            125000,
            150000,
            175000,
            200000,
            225000,
            250000,
            275000,
            300000,
            325000,
            350000,
            375000,
            400000,
            450000,
            500000,
            550000,
            600000,
            650000,
            700000,
            750000,
            800000,
            900000,
            1000000,
            1250000,
            1500000,
            2000000,
            2500000,
            3000000,
            3500000,
            4000000,
            4500000,
            5000000
        )

        val result = webclient.get()
            .uri("https://funda.nl/koop/$CITY/0-350000")
            .accept(MediaType.APPLICATION_XML)
            .retrieve()
            .awaitBody<String>()

        // parse first result
        val document: Document = Jsoup.parse(result)
        val regex = """^/koop/$CITY/[0-9]+-[0-9]+/p[0-9]+/$"""
        val links = document.select("[href~=$regex]").not("[rel]")
        val highestPage = links.map {
            val tempList = it.attr("href").split("/")
            tempList[tempList.size - 2]
        }.last()
        // get the relevant links
        println(highestPage)
        val allLinks = generateSequence(1) { it + 1 }.take(
            highestPage.split("p").last().toInt()
        ).map { "https://funda.nl/koop/$CITY/0-350000/p$it" }
        // search result media and search result promo add up to the total amount of ads per page, get them independently?
        val houses = allLinks.take(4).toList().map {
            async {
                val streetList: MutableList<String> = mutableListOf()
                val cityList: MutableList<String> = mutableListOf()
                val houseNumberList: MutableList<String> = mutableListOf()
                val zipCodeList: MutableList<String> = mutableListOf()
                val squareMeterList: MutableList<String> = mutableListOf()
                val priceList: MutableList<String> = mutableListOf()
                val numberOfRoomsList: MutableList<String> = mutableListOf()
                val linkList: MutableList<String> = mutableListOf()
                val specificResult = webclient.get()
                    .uri(it)
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .awaitBody<String>()
                val houseDocument: Document = Jsoup.parse(specificResult)
                val children = houseDocument.select(".search-result-content-inner")
                NodeTraversor.traverse({ node, _ ->
                    val street: String
                    val houseNumber: String
                    val city: String
                    val zipCode: String
                    val squareMeters: String
                    val price: String
                    val numberOfRooms: String
                    if (node is Element) {
                        val text = node.text()
                        when (node.className()) {
                            "search-result__header-subtitle fd-m-none" -> {
                                val values = text.split(" ")
                                city = values[values.size - 1]
                                zipCode = values[0] + values[1]
                                zipCodeList.add(zipCode)
                                cityList.add(city)
                                val href = node.parent()?.attr("href")
                                linkList.add(href!!)
                            }
                            "search-result__header-title fd-m-none" -> {
                                val values = text.split(" ")
                                val houseNumberIndex = findFirstHouseNumberIndex(values)
                                street = values.subList(0, houseNumberIndex).joinToString(" ")
                                houseNumber = values.subList(houseNumberIndex, values.size).joinToString("-")
                                streetList.add(street)
                                houseNumberList.add(houseNumber)
//                                println("house found! $street + $houseNumber + $city + $zipCode")
                            }
                            "search-result-kenmerken" -> {
                                squareMeters = node.children()[0].text().split(" ")[0]
                                numberOfRooms = node.children()[1].text().split(" ")[0]
                                numberOfRoomsList.add(numberOfRooms)
                                squareMeterList.add(squareMeters)
                            }
                            "search-result-price" -> {
                                price = text.split(" ")[1]
                                priceList.add(price)
                            }
                            else -> {}
                        }
                    }

                }, children)
                val housesPerPage = zip(
                    streetList,
                    houseNumberList,
                    zipCodeList,
                    cityList,
                    priceList,
                    squareMeterList,
                    numberOfRoomsList,
                    linkList,
                    transform = {
                        BuyHouseDTO(ZipCodeHouseNumber(it[2], it[1]), it[0], it[3], it[4], it[5], it[6], it[7])
                    })
                housesPerPage.forEach { println("dto $it") }
                return@async housesPerPage
//                return@async houseDocument.select(".search-result-media a[data-object-url-tracking*=\"resultlist\"]").toList()
            }
        }.awaitAll().flatten()
        houses.forEach {
            print("hello funda $it")
        }
//        TODO("use search-result-content-inner instead of the search result media")
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

    suspend inline fun String.parseHousePage(
        parser: (String) -> List<String>
    ): List<String> {
        return parser(this)
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

inline fun <T, V> zip(vararg lists: List<T>, transform: (List<T>) -> V): List<V> {
    val minSize = lists.map(List<T>::size).min()
    val list = ArrayList<V>(minSize)

    val iterators = lists.map { it.iterator() }
    var i = 0
    while (i < minSize) {
        list.add(transform(iterators.map { it.next() }))
        i++
    }

    return list
}

fun findFirstHouseNumberIndex(literal: String): Int {
    return literal.split(" ").takeWhile { isAlpha(it) }.size - 1
}

fun findFirstHouseNumberIndex(literal: List<String>): Int {
    return literal.takeWhile {
        val what = isAlpha(it) or !isNumeric(it)
        println("$it + $what")
        what
    }.size
}
