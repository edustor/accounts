package ru.edustor.accounts.oauth2.rest

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.edustor.commons.exceptions.HttpRequestProcessingException

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController {
    @RequestMapping
    fun token(@RequestParam payload: Map<String, String>) {
        val grantType = payload["grant_type"] ?:
                throw HttpRequestProcessingException(HttpStatus.BAD_REQUEST,
                        mapOf(
                                "error" to "invalid_request",
                                "error_description" to "grant_type was not provided"
                        )
                )
    }
}