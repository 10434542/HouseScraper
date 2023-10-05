package org.ray.housewebscraper.util

import arrow.core.Either
import arrow.core.getOrElse

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