package ru.edustor.accounts.model

import ru.edustor.accounts.util.genRandomToken
import java.time.Instant
import javax.annotation.Nullable
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
        @ManyToOne(optional = false)
        val account: Account,

        @Suppress("JoinDeclarationAndAssignment")
        @Basic(optional = false)
        @Id val token: String = genRandomToken(),

        val scope: String,
        var lastUsed: Instant = Instant.now()
)