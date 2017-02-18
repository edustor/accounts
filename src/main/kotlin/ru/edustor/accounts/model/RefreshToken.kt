package ru.edustor.accounts.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.edustor.accounts.util.genRandomToken
import java.time.Instant
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class RefreshToken() {
    @Id var id: String = UUID.randomUUID().toString()

    lateinit var token: String
    lateinit var scope: String
    lateinit var lastUsed: Instant

    //todo: Cascade updates
    @OneToOne(cascade = arrayOf(CascadeType.REMOVE))
    @OnDelete(action = OnDeleteAction.CASCADE)
    lateinit var account: Account

    constructor(account: Account, scope: String) : this() {
        this.account = account
        this.token = genRandomToken()
        this.scope = scope
        lastUsed = Instant.now()
    }
}