package ru.edustor.accounts.exceptions.oauth2

import org.springframework.http.HttpStatus
import ru.edustor.accounts.exceptions.HttpRequestProcessingException

open class OAuthException(error: String, errorDescription: String, status: HttpStatus = HttpStatus.BAD_REQUEST) : HttpRequestProcessingException(status, mapOf(
        "error" to error,
        "error_description" to errorDescription
))