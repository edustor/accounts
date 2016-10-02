package ru.edustor.accounts.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class Account {
    @Id var id: String = UUID.randomUUID().toString()
    var email: String? = null
    @Indexed var googleSub: String? = null
}