package ru.edustor.accounts.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.edustor.accounts.model.Account
import ru.edustor.accounts.model.RefreshToken

@Repository
interface RefreshTokenRepository : MongoRepository<RefreshToken, String> {
    fun findByToken(token: String): RefreshToken?
}