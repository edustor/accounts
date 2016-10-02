package ru.edustor.accounts.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import ru.edustor.accounts.util.genRandomToken

@Document
class RefreshToken() {
    @Id lateinit var token: String
    @DBRef @Indexed lateinit var account: Account

    constructor(account: Account) : this() {
        this.account = account
        this.token = genRandomToken()
    }
}