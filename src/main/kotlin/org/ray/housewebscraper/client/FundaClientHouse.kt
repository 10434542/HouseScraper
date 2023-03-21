package org.ray.housewebscraper.client

import arrow.core.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor
import org.ray.housewebscraper.config.FundaConfigurationProperties
import org.ray.housewebscraper.funda.findFirstHouseNumberIndex
import org.ray.housewebscraper.funda.tryAwaitBodyOrElseEither
import org.ray.housewebscraper.funda.zip
import org.ray.housewebscraper.model.BuyHouseDTO
import org.ray.housewebscraper.model.ZipCodeHouseNumber
import org.ray.housewebscraper.model.HouseWebClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
@Qualifier("Funda")
class FundaClientHouse(private val webClient: WebClient, private val configuration: FundaConfigurationProperties) :
    HouseWebClient {

    override suspend fun getHousesByCityWithinRange(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int,
    ): Either<Throwable, List<BuyHouseDTO>> {
        val returnValue = coroutineScope {
            val url = "${configuration.url}/koop/$cityName/$minimum-$maximum"
            val result = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .tryAwaitBodyOrElseEither<String>()
            val outcome = result.flatMap { str ->

                val regex = """^/koop/$cityName/[0-9]+-[0-9]+/p[0-9]+/$"""
                val pageNumber = Jsoup.parse(str).select("[href~=$regex]").not("[rel]")
                    .map {
                        val tempList = it.attr("href").split("/")
                        tempList[tempList.size - 2]
                    }.last()
                val relevantHouses = generateSequence(1) { it + 1 }.take(
                    pageNumber.split("p").last().toInt()
                ).map {
                    "$url/p$it"
                }.take(pages).toList().map {
                    async {
                        val streetList: MutableList<String> = mutableListOf()
                        val cityList: MutableList<String> = mutableListOf()
                        val houseNumberList: MutableList<String> = mutableListOf()
                        val zipCodeList: MutableList<String> = mutableListOf()
                        val squareMeterList: MutableList<String> = mutableListOf()
                        val priceList: MutableList<String> = mutableListOf()
                        val numberOfRoomsList: MutableList<String> = mutableListOf()
                        val linkList: MutableList<String> = mutableListOf()
                        val specificResult = webClient.get()
                            .uri(it)
                            .accept(MediaType.APPLICATION_XML)
                            .retrieve()
                            .tryAwaitBodyOrElseEither<String>()
                            .map lit@{ something ->
                                val children = Jsoup.parse(something).select(".search-result-content-inner")
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
                                                houseNumber =
                                                    values.subList(houseNumberIndex, values.size).joinToString("-")
                                                streetList.add(street)
                                                houseNumberList.add(houseNumber)
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
                                return@lit housesPerPage
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
        return returnValue
    }
}


@Component
class FundaVisitor() : NodeVisitor {
    override fun head(node: Node, depth: Int) {
        TODO("Not yet implemented")
    }
}

