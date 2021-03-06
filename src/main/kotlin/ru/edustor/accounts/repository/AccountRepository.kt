package ru.edustor.accounts.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.edustor.accounts.model.Account

@Repository
interface AccountRepository : JpaRepository<Account, String> {
    fun findByGoogleSub(sub: String): Account?
}