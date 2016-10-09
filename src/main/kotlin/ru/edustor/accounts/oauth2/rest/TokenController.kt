package ru.edustor.accounts.oauth2.rest

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.edustor.accounts.model.Account
import ru.edustor.accounts.model.RefreshToken
import ru.edustor.accounts.oauth2.providers.google.GoogleProvider
import ru.edustor.accounts.repository.AccountRepository
import ru.edustor.accounts.repository.RefreshTokenRepository
import ru.edustor.accounts.exceptions.HttpRequestProcessingException
import ru.edustor.accounts.exceptions.oauth2.InvalidGrantException
import ru.edustor.accounts.exceptions.oauth2.MissingArgumentException
import ru.edustor.accounts.exceptions.oauth2.OAuthException
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.*

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController(val googleProvider: GoogleProvider, val refreshTokenRepository: RefreshTokenRepository) {
    val logger = LoggerFactory.getLogger(TokenController::class.java)

    val TOKEN_EXPIRE_IN = 10 * 60 // Seconds

    val signkey: PrivateKey

    init {
        val keyBytes = File("keys/jwk.der").readBytes()
        val spec = PKCS8EncodedKeySpec(keyBytes)
        signkey = KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    @RequestMapping
    fun token(@RequestParam payload: Map<String, String>): Map<String, Any> {
        val grantType = payload["grant_type"] ?: throw MissingArgumentException("grant_type")

        val (account, scope) = when (grantType) {
            "password" -> processPasswordGrant(payload)
            "refresh_token" -> processRefreshTokenGrant(payload)
            else -> throw OAuthException("unsupported_grant_type", "$grantType is not recognized")
        }

        val token = makeToken(account, scope)

        val resp = mutableMapOf(
                "token" to token,
                "expires_in" to TOKEN_EXPIRE_IN,
                "scope" to scope
        )

        val scopeList = scope.split(" ")
        if ("offline" in scopeList && grantType != "refresh_token") {
            val rt = RefreshToken(account, scope)
            refreshTokenRepository.save(rt)
            resp["refresh_token"] = rt.token
        }

        logger.info("Issuing token for ${account.id} with grant type $grantType and scope \"$scope\"")

        return resp
    }

    fun processPasswordGrant(payload: Map<String, String>): Pair<Account, String> {
        val scope = payload["scope"] ?: ""

        val username = payload["username"] ?: throw MissingArgumentException("username")
        val password = payload["password"] ?: throw MissingArgumentException("password")

        val account = when (username) {
            "@google" -> googleProvider.authenticateWithGoogle(password)
            else -> throw InvalidGrantException("Unknown username")
        }

        return account to scope
    }

    fun processRefreshTokenGrant(payload: Map<String, String>): Pair<Account, String> {
        val tokenStr = payload["refresh_token"] ?: throw MissingArgumentException("refresh_token")
        val token = refreshTokenRepository.findByToken(tokenStr) ?: throw InvalidGrantException("Invalid or expired refresh token")
        token.lastUsed = Instant.now()
        refreshTokenRepository.save(token)
        return token.account to token.scope
    }

    fun makeToken(account: Account, scope: String): String {

        try {
            val token = Jwts.builder()
                    .setSubject(account.id)
                    .claim("scope", scope)
                    .setExpiration(Date(System.currentTimeMillis() + TOKEN_EXPIRE_IN * 1000))
                    .setIssuedAt(Date())
                    .signWith(SignatureAlgorithm.RS256, signkey)
                    .compact()
            return token
        } catch (e: Exception) {
            throw HttpRequestProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token")
        }
    }
}