package ru.edustor.accounts.model

import ru.edustor.accounts.util.genRandomToken
import java.time.Instant
import javax.annotation.Nullable
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken() {
    @Suppress("JoinDeclarationAndAssignment")
    @Nullable
    @Basic(optional = false)
    @Id lateinit var token: String

    @ManyToOne
    lateinit var account: Account

    lateinit var scope: String
    var lastUsed: Instant = Instant.now()

    constructor(account: Account, scope: String) : this() {
        this.account = account
        token = genRandomToken()
        this.scope = scope
    }
}