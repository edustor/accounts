package ru.edustor.accounts.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.edustor.accounts.model.RefreshToken

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
}