package ru.edustor.accounts.exceptions

import org.springframework.http.HttpStatus
import ru.edustor.accounts.exceptions.HttpRequestProcessingException

class NotFoundException(override val message: String? = null, override val cause: Throwable? = null) :
        HttpRequestProcessingException(HttpStatus.NOT_FOUND, message, cause)
