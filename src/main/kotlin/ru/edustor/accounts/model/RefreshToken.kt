package ru.edustor.accounts.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.edustor.accounts.util.genRandomToken
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class RefreshToken() {

    @Id
    var id: String = UUID.randomUUID().toString()

    lateinit var token: String
    @OneToOne(cascade = arrayOf(CascadeType.REMOVE))
    @OnDelete(action = OnDeleteAction.CASCADE)
    lateinit var account: Account

    constructor(account: Account) : this() {
        this.account = account
        this.token = genRandomToken()
    }
}