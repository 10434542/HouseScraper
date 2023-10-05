package org.ray.housewebscraper.util

import arrow.core.Either
import org.apache.commons.lang3.StringUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

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

// TODO: use either for error-handling.
suspend inline fun <reified A : Any> WebClient.ResponseSpec.tryAwaitBodyOrElseEither(): Either<Throwable, A> {
    return Either.catch<A> {
        awaitBody()
    }.onLeft {
        println(it.message)
    }
}

fun findFirstHouseNumberIndex(literal: List<String>): Int {
    return literal.takeWhile {
        val what = StringUtils.isAlpha(it) or !StringUtils.isNumeric(it)
        println("$it + $what")
        what
    }.size
}