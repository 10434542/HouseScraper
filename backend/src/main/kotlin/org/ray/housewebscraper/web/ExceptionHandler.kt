package org.ray.housewebscraper.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    suspend fun handleException(ex: NoSuchElementException): ResponseEntity<Any> {
        return ResponseEntity.notFound().build()
    }
}