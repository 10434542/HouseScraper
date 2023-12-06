package org.ray.housewebscraper

import arrow.core.*
import com.nimbusds.oauth2.sdk.util.StringUtils.isAlpha
import com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Comment
import org.jsoup.select.Elements
import org.jsoup.select.NodeFilter
import org.junit.jupiter.api.Test
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.HouseStatus
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.util.*
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.util.MimeTypeUtils
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient


private const val CITY = """haarlem"""

private const val s1 = "street-name-house-number"

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
                .uri("https://funda.nl/zoeken/koop?selected_area=[\"$CITY\"]&price=\"0-500000\"")
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .tryAwaitBodyOrElseEither<String>()
            var bla = Elements(1)
            result.onRight {
                bla = Jsoup.parse(it).select("[class^=border-light-2 mb-4 border-b pb-4]")
            }
            println(bla.size)
            bla.forEach { println(it) }

            val outcome = result.flatMap { str ->
                // parse first result
                val document = Jsoup.parse(str)
                val allElements = document.select("a")
                // Filter the selected elements based on tabindex attribute
                val maxPages = allElements.filter { element ->
                    element.attr("tabindex") == "0" && element.text().isNumeric()
                }.maxOfOrNull {
                    it.text().toInt()
                }
                val relevantHouses = generateSequence(1) { it + 1 }.take(maxPages!!)
                    .map { "https://funda.nl/zoeken/koop?selected_area=[\"$CITY\"]&price=\"0-500000\"&search_result=$it" }
                    .take(2).toList().map {
                        async {
                            val specificResult = webclient.get()
                                .uri(it)
                                .accept(MediaType.APPLICATION_XML)
                                .retrieve()
                                .tryAwaitBodyOrElseEither<String>()
                                .map lit@{ something ->
                                    val traversor = traversor {
                                        root = Elements(Jsoup.parse(something)
                                            .select("[class^=border-light-2 mb-4 border-b]").filter(NodeFilter{node, depth ->
                                                if (node.chi is Comment) {
                                                }
                                            })

                                        //Somehow loop over all the house divs
                                        filter {
                                            attribute {
                                                cssAttributeKey = "data-test-id"
                                                cssAttributeValue = "street-name-house-number"
                                            }
                                            onSuccess {
                                                text()
                                            }
                                        }
                                        filter {
                                            attribute {
                                                cssAttributeKey = "data-test-id"
                                                cssAttributeValue = "postal-code-city"
                                            }
                                            onSuccess {
                                                text()
                                            }
                                        }

                                        filter {
                                            attribute {
                                                cssAttributeKey = "data-test-id"
                                                cssAttributeValue = "price-sale"
                                            }
                                            onSuccess {
                                                text()
                                            }
                                        }

                                        filter {
                                            attribute {
                                                // square meters, number of rooms and the energylabel
                                                cssAttributeKey = "class"
                                                cssAttributeValue = "mt-1 flex h-6 min-w-0 flex-wrap overflow-hidden"
                                            }
                                            onSuccess {
                                                children().text()
                                            }
                                        }

                                        filter {
                                            attribute {
                                                cssAttributeKey = "class"
                                                cssAttributeValue = "text-blue-2 visited:text-purple-1 cursor-pointer"
                                            }

                                            onSuccess {
                                                attr("href")
                                            }
                                        }

                                        filter {
                                            attribute {
                                                cssAttributeKey = "class"
                                                cssAttributeValue =
                                                    "mb-1 mr-1 rounded-sm px-1 py-0.5 text-xs font-semibold bg-red-1 text-white"
                                            }

                                            onSuccess {
                                                text()
                                            }

                                            onFailure {
                                                "None"
                                            }


                                        }

                                    }
                                    val buyHouseDocuments = traversor.traverseNode {
                                        val streetHouseNumber = this["street-name-house-number"].toString().split(" ")
                                        val houseNumberIndex = findFirstHouseNumberIndex(streetHouseNumber)
                                        val street = streetHouseNumber.slice(0..(houseNumberIndex)).joinToString(" ")

                                        val houseNumber =
                                            streetHouseNumber.slice((houseNumberIndex)..<streetHouseNumber.size)
                                                .joinToString(" ")
                                        val zipCodeCity = this["postal-code-city"].toString().split(" ")
                                        val realZipCode = zipCodeCity[0] + zipCodeCity[1]
                                        val city = zipCodeCity[zipCodeCity.size - 1]
                                        val price = this["price-sale"].toString().replace(Regex("[^0-9]+"), "")
                                        val surfaceRoomEnergyLabel =
                                            this["mt-1 flex h-6 min-w-0 flex-wrap overflow-hidden"].toString()
                                                .split(" ")
                                        val surface = surfaceRoomEnergyLabel[0] + surfaceRoomEnergyLabel[1]
                                        val numberOfRooms = surfaceRoomEnergyLabel[surfaceRoomEnergyLabel.size - 2]
////                                    val energyLabel = surfaceRoomsEnergyLabel[3] // TODO: add this one to the DTOs and stuff
                                        val link = this["text-blue-2 visited:text-purple-1 cursor-pointer"].toString()
                                        val status =
                                            getHouseStatus(this["mb-1 mr-1 rounded-sm px-1 py-0.5 text-xs font-semibold bg-red-1 text-white"].toString())
                                        return@traverseNode BuyHouseDTO(
                                            ZipCodeHouseNumber(realZipCode, houseNumber),
                                            street,
                                            city,
                                            price,
                                            surface,
                                            numberOfRooms,
                                            link
                                        )
                                    }
                                    buyHouseDocuments.forEach { someHouse ->
                                        println("yo there is something $someHouse")
                                    }
                                    return@lit buyHouseDocuments
                                }
                            specificResult.onRight { house ->
                                house.forEach { println("found a house $it") }
                            }
                            return@async specificResult
                        }
                    }.awaitAll()
                relevantHouses.forEach { house ->
                    println("amount of houses found is ${house.getOrNull()}")
                }
                return@flatMap relevantHouses.flattenToEither()
            }
            return@coroutineScope outcome
        }

        returnValue.getOrNull()?.forEach {
            println(it)
        }
        println("value in right is ${returnValue.getOrNull()}")

        println("if this is true an error has occurred ${returnValue.isLeft()}")

    }
}

fun getHouseStatus(s: String): HouseStatus {
    return when (s) {
        "Onder option" -> HouseStatus.UNDER_OPTION
        "Verkocht onder voorbehoud" -> HouseStatus.SOLD_SUBJECT_TO_CONTRACT
        "Onder bod" -> HouseStatus.UNDER_BIDDING
        "Verkocht" -> HouseStatus.SOLD
        else -> HouseStatus.AVAILABLE
    }
}

fun <A, B> Collection<Either<A, Collection<B>>>.flattenToEither(): Either<A, Collection<B>> {
    return this.fold(Either.Right(listOf())) { accumulator, either ->
        accumulator.fold(
            { Either.Left(it) },
            {
                either.fold(
                    { Either.Left(it) },
                    { Either.Right(it + accumulator.getOrElse { listOf() }) }
                )
            }
        )
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
