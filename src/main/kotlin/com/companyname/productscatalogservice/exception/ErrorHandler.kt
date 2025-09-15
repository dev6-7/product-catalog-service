package com.companyname.productscatalogservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandler {
    data class Err(val error: String?)

    @ExceptionHandler(Conflict::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun conflict(ex: Conflict) = Err(ex.message)

    @ExceptionHandler(NotFound::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(ex: NotFound) = Err(ex.message)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(ex: IllegalArgumentException) = mapOf("error" to ex.message)
}

class Conflict(msg: String) : RuntimeException(msg)
class NotFound(msg: String) : RuntimeException(msg)

