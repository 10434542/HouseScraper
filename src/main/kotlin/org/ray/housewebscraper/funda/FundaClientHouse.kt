package org.ray.housewebscraper.funda

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.ray.housewebscraper.model.BuyHouse
import org.ray.housewebscraper.model.HouseWebClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class FundaClientHouse(val webClient: WebClient) : HouseWebClient {
    suspend fun getBuyHouseByName(city: String): Unit {
        println("wew")
    }

    override suspend fun getBuyHouseByCity(cityName: String): BuyHouse {
        val response = webClient.get()
            .uri("/koop/$cityName/0-350000/")
            .accept(MediaType.APPLICATION_XML)
            .retrieve()
            .awaitBody<String>()
        val document: Document = Jsoup.parse(response)
        val regex = """^/koop/$cityName/[0-9]+-[0-9]+/p[0-9]+/$"""
        val links = document.select("[href~=$regex]").not("[rel]")
        val highestPage = links.map {
            val tempList = it.attr("href").split("/")
            tempList[tempList.size - 2]
        }.last()
        val allLinks = generateSequence(1) { it + 1 }.take(
            highestPage.split("p").last().toInt()
        ).map { "/koop/$cityName/0-350000/p$it" }
        val results =
            coroutineScope {

                allLinks.map {
                    async(Dispatchers.IO) {
                        // this is it!
                        println("starting $it on thread ${Thread.currentThread()}")
                val whatever = webClient.get()
                    .uri(it)
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .awaitBody<String>()
                    }
                }
            }.toList().awaitAll().take(2).map {
                coroutineScope {

                }
            }

//        val houses = results.awaitAll()
        TODO("Not yet implemented")
    }

    override suspend fun getHousesByCityWithinRange(cityName: String, minimum: Long, maximum: Long) {
        val url = "https://funda.nl/koop/$cityName/$minimum-$maximum"
        val result = webClient.get()
            .uri(url)
            .accept(MediaType.APPLICATION_XML)
            .retrieve()
            .awaitBody<String>()
        val document: Document = Jsoup.parse(result)

        val regex = """^/koop/$cityName/[0-9]+-[0-9]+/p[0-9]+/$"""
        val links = document.select("[href~=$regex]").not("[rel]")
        val highestPage = links.map {
            val tempList = it.attr("href").split("/")
            tempList[tempList.size - 2]
        }.last()
        val allLinks = generateSequence(1) { it + 1 }.take(
            highestPage.split("p").last().toInt()
        ).map { "$url/p$it" }
    }

    override suspend fun getBuyHouseByCityAndPriceRange(cityName: String, range: LongRange) {
        TODO("Not yet implemented")
    }

    private suspend fun getPagesFromUrl(url: String): List<String> {

    }
}
