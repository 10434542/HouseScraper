package org.ray.housewebscraper

import arrow.core.Either
import arrow.core.getOrElse
import com.nimbusds.oauth2.sdk.util.StringUtils.isAlpha
import com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.persistence.BuyHouseDocument
import org.ray.housewebscraper.util.*
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
        val returnValue = coroutineScope {
            val result = webclient.get()
                .uri("https://funda.nl/zoeken/koop?selected_area=[\"$CITY\"]&price=\"0-1000000\"")
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .awaitBody<String>()

            // parse first result
            val document = Jsoup.parse(result)
            val allElements = document.select("a")
            // Filter the selected elements based on tabindex attribute
            val maxPages = allElements.filter { element ->
                element.attr("tabindex") == "0" && element.text().isNumeric()
            }.maxOfOrNull {
                it.text().toInt()
            }
            val outcome = result.flatMap {str ->
                val relevantHouses = generateSequence(1) { it + 1 }.take(maxPages!!)
                    .map { "https://funda.nl/zoeken/koop?selected_area=[\"$CITY\"]&price=\"0-1000000\"&search_result=$it" }
                    .take(1).toList().map {
                        async {
                            val specificResult = webclient.get()
                                .uri(it)
                                .accept(MediaType.APPLICATION_XML)
                                .retrieve()
                                .tryAwaitBodyOrElseEither<String>()
                                .map lit@{ something ->
                                    val buyHouseDocuments = traversor {
                                        root = Jsoup.parse(something).select("div.pt-4")
                                        filter {
                                            attribute {
                                                cssAttributeKey = "data-test-id"
                                                cssAttributeValue = "street-name-house-number"
                                            }
                                            extractor {
                                                text()
                                            }
                                        }
                                        filter {
                                            attribute {
                                                cssAttributeKey = "data-test-id"
                                                cssAttributeValue = "postal-code-city"
                                            }
                                            extractor {
                                                text()
                                            }
                                        }

                                        filter {
                                            attribute {
                                                cssAttributeKey = "data-test-id"
                                                cssAttributeValue = "price-sale"
                                            }
                                            extractor {
                                                text()
                                            }
                                        }

                                        filter {
                                            attribute {
                                                // square meters, number of rooms and the energylabel
                                                cssAttributeKey = "class"
                                                cssAttributeValue = "mt-1 flex h-6 min-w-0 flex-wrap overflow-hidden"
                                            }
                                            extractor {
                                                children().text()
                                            }
                                        }

                                        filter {
                                            attribute {
                                                cssAttributeKey = "class"
                                                cssAttributeValue = "text-blue-2 visited:text-purple-1 cursor-pointer"
                                            }

                                            extractor {
                                                attr("href")
                                            }
                                        }
                                    }.traverse()
                                        .collect {
                                            val streetHouseNumber = it[0].split(" ")
                                            val houseNumberIndex = findFirstHouseNumberIndex(streetHouseNumber)
                                            println("streetHouseNumber $streetHouseNumber and the houseNumber is ${streetHouseNumber[houseNumberIndex]}")
                                            val street =
                                                streetHouseNumber.slice(0..houseNumberIndex).joinToString { " " }
                                            val houseNumber =
                                                streetHouseNumber.slice((houseNumberIndex + 1)..<streetHouseNumber.size)
                                                    .joinToString { " " }
                                            val zipCodeCity = it[1].split(" ")
                                            val zipCode = zipCodeCity.slice(0..1).joinToString { " " }
                                            val city = zipCodeCity[zipCodeCity.size - 1]
                                            val price = it[2]
                                            val surfaceRoomsEnergyLabel = it[3].split(" ")
                                            val surface = surfaceRoomsEnergyLabel.slice(0..1).joinToString { " " }
                                            val numberOfRooms = surfaceRoomsEnergyLabel[2]
//                                val energyLabel = surfaceRoomsEnergyLabel[3] // TODO: add this one to the DTOs and stuff
                                            val link = it[4]
                                            return@collect BuyHouseDTO(
                                                ZipCodeHouseNumber(zipCode, houseNumber),
                                                street,
                                                city,
                                                price,
                                                surface,
                                                numberOfRooms,
                                                link
                                            )
                                        }
                                    return@lit buyHouseDocuments
                                }
                            return@async specificResult
                        }
                    }.awaitAll()
                val flattenedEither: Either<Throwable, List<BuyHouseDTO>> =
                    relevantHouses.fold(Either.Right(listOf())) { acc, either ->
                        acc.fold(
                            { Either.Left(it) },
                            {
                                either.fold(
                                    { Either.Left(it) },
                                    { Either.Right(it + acc.getOrElse { listOf() }) })
                            })
                    }
                return@flatMap flattenedEither
            }
            return@coroutineScope outcome
        }


//        val flattenedEither: Either<Throwable, List<BuyHouseDTO>> =
//            relevantHouses.fold(Either.Right(listOf())) { acc, either ->
//                acc.fold(
//                    { Either.Left(it) },
//                    {
//                        either.fold(
//                            { Either.Left(it) },
//                            { Either.Right(it + acc.getOrElse { listOf() }) })
//                    })
//            }
//        return@flatMap flattenedEither
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

fun String.isNumeric(): Boolean {
    return this.all { char -> char.isDigit() }
}
