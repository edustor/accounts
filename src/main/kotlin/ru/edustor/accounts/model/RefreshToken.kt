package ru.edustor.accounts.model

import org.springframework.data.mongodb.core.index.Indexed
import ru.edustor.accounts.util.genRandomToken
import java.time.Instant

class RefreshToken() {
    @Indexed lateinit var token: String
    lateinit var scope: String
    @Indexed lateinit var lastUsed: Instant

    constructor(scope: String) : this() {
        this.token = genRandomToken()
        this.scope = scope
        lastUsed = Instant.now()
    }
}