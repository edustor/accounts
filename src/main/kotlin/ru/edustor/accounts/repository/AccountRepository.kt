package ru.edustor.accounts.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.edustor.accounts.model.Account

@Repository
interface AccountRepository : MongoRepository<Account, String> {
    fun findByGoogleSub(sub: String): Account?
    fun findByRefreshTokensTokenIn(refreshToken: String): Account?
}