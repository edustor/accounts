package ru.edustor.accounts.oauth2.rest

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.edustor.accounts.model.Account
import ru.edustor.accounts.model.RefreshToken
import ru.edustor.accounts.oauth2.providers.google.GoogleProvider
import ru.edustor.accounts.repository.RefreshTokenRepository
import ru.edustor.commons.exceptions.HttpRequestProcessingException
import ru.edustor.commons.exceptions.oauth2.InvalidGrantException
import ru.edustor.commons.exceptions.oauth2.MissingArgumentException
import ru.edustor.commons.exceptions.oauth2.OAuthException
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController(val googleProvider: GoogleProvider, val refreshTokenRepository: RefreshTokenRepository) {


    val TOKEN_EXPIRE_IN = 60 * 60 // Seconds

    val signkey: PrivateKey

    init {
        val keyBytes = File("keys/jwk.der").readBytes()
        val spec = PKCS8EncodedKeySpec(keyBytes)
        signkey = KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    @RequestMapping
    fun token(@RequestParam payload: Map<String, String>): Map<String, Any> {
        val grantType = payload["grant_type"] ?: throw MissingArgumentException("grant_type")
        val scope = payload["scope"] ?: ""

        val scopeList = scope.split(" ")

        val account = when (grantType) {
            "password" -> processPasswordGrant(payload)
            else -> throw OAuthException("unsupported_grant_type", "$grantType is not recognized")
        }

        val token = makeToken(account, scope)

        val resp = mutableMapOf(
                "token" to token,
                "expires_in" to TOKEN_EXPIRE_IN,
                "scope" to scope
        )

        if ("offline" in scopeList) {
            val rt = RefreshToken(account)
            refreshTokenRepository.save(rt)
            resp["refresh_token"] = rt.token
        }

        return resp
    }

    fun processPasswordGrant(payload: Map<String, String>): Account {
        val username = payload["username"] ?: throw MissingArgumentException("username")
        val password = payload["password"] ?: throw MissingArgumentException("password")

        val account = when (username) {
            "@google" -> googleProvider.authenticateWithGoogle(password)
            else -> throw InvalidGrantException("Unknown username")
        }

        return account
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