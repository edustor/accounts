package ru.edustor.accounts.model

import ru.edustor.accounts.util.genRandomToken
import java.time.Instant

class RefreshToken() {
    @Suppress("JoinDeclarationAndAssignment")
    lateinit var token: String
    lateinit var scope: String
    var lastUsed: Instant = Instant.now()

    constructor(scope: String) : this() {
        token = genRandomToken()
        this.scope = scope
    }
}