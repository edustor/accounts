package ru.edustor.accounts.exceptions.oauth2

import org.springframework.http.HttpStatus
import ru.edustor.accounts.exceptions.HttpRequestProcessingException

open class OAuthException(error: String, errorDescription: String) : HttpRequestProcessingException(HttpStatus.BAD_REQUEST, mapOf(
        "error" to error,
        "error_description" to errorDescription
))