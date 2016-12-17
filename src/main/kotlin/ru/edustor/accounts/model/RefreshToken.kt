package ru.edustor.accounts.model

import ru.edustor.accounts.util.genRandomToken
import java.time.Instant

class RefreshToken() {
    lateinit var token: String
    lateinit var scope: String
    lateinit var lastUsed: Instant

    constructor(scope: String) : this() {
        this.token = genRandomToken()
        this.scope = scope
        lastUsed = Instant.now()
    }
}