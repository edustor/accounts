package ru.edustor.accounts.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.persistence.Id

@Document
class Account {
    @Id var id: String = UUID.randomUUID().toString()

    var email: String? = null
    var googleSub: String? = null

    @JsonIgnore
    var refreshTokens: MutableList<RefreshToken> = mutableListOf()
}