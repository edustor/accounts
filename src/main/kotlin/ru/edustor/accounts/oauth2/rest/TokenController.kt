package ru.edustor.accounts.oauth2.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController {
    @RequestMapping
    fun token(@RequestParam payload: Map<String, String>) {
        val grantType = payload.get("grant_type")
    }
}