package ru.edustor.accounts.oauth2.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.edustor.accounts.oauth2.providers.google.GoogleProvider
import ru.edustor.commons.exceptions.oauth2.InvalidGrantException
import ru.edustor.commons.exceptions.oauth2.MissingArgumentException

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController(val googleProvider: GoogleProvider) {
    @RequestMapping
    fun token(@RequestParam payload: Map<String, String>) {
        val grantType = payload["grant_type"] ?: throw MissingArgumentException("grant_type")

        when (grantType) {
            "password" -> processPasswordGrant(payload)
        }
    }

    fun processPasswordGrant(payload: Map<String, String>) {
        val username = payload["username"] ?: throw MissingArgumentException("username")
        val password = payload["password"] ?: throw MissingArgumentException("password")

        when (username) {
            "@google" -> googleProvider.processIdToken(password)
            else -> throw InvalidGrantException("Unknown username")
        }

    }
}