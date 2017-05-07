package ru.edustor.accounts.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "accounts")
class Account {
    @Id var id: String = UUID.randomUUID().toString()

    var email: String? = null
    var googleSub: String? = null

    @OneToMany(targetEntity = RefreshToken::class, mappedBy = "account",
            cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
    @Basic(fetch = FetchType.LAZY)
    val refreshTokens: MutableList<RefreshToken> = mutableListOf()
}