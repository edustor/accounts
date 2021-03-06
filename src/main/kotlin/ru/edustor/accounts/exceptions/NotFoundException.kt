package ru.edustor.accounts.exceptions

import org.springframework.http.HttpStatus

class NotFoundException(override val message: String? = null, override val cause: Throwable? = null) :
        HttpRequestProcessingException(HttpStatus.NOT_FOUND, message, cause)
