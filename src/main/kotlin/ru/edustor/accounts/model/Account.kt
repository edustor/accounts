package ru.edustor.accounts.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Account {
    @Id
    var id: String = UUID.randomUUID().toString()

    var email: String? = null
    var googleSub: String? = null
}