package org.ray.housewebscraper.model

import arrow.core.Either
import org.ray.housewebscraper.util.tryAwaitBodyOrElseEither
import org.springframework.web.reactive.function.client.WebClient

interface HouseWebClient {
    suspend fun getHousesByCityWithinRange(
        cityName: String,
        minimum: Long,
        maximum: Long,
        pages: Int
    ): Either<Throwable, List<BuyHouseDTO>>

//    fun getClient(): WebClient
//
//
//    suspend fun getFirstPage(url: String) : Either<Throwable, String> = getClient()
//            .get()
//            .uri(url)
//            .retrieve()
//            .tryAwaitBodyOrElseEither<String>()

}
