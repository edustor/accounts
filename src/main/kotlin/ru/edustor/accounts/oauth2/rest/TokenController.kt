package ru.edustor.accounts.oauth2.rest

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.edustor.accounts.exceptions.HttpRequestProcessingException
import ru.edustor.accounts.exceptions.oauth2.InvalidGrantException
import ru.edustor.accounts.exceptions.oauth2.MissingArgumentException
import ru.edustor.accounts.exceptions.oauth2.OAuthException
import ru.edustor.accounts.model.Account
import ru.edustor.accounts.model.RefreshToken
import ru.edustor.accounts.oauth2.providers.google.GoogleProvider
import ru.edustor.accounts.repository.RefreshTokenRepository
import java.security.PrivateKey
import java.security.Security
import java.time.Instant
import java.util.*

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController(
        val googleProvider: GoogleProvider,
        val refreshTokenRepository: RefreshTokenRepository,
        environment: Environment) {

    val logger: Logger = LoggerFactory.getLogger(TokenController::class.java)
    val signKey: PrivateKey
    val systemScopes = arrayOf("internal")

    init {
        val pemKeyBase64 = environment.getRequiredProperty("edustor.accounts.jwk-key")
        val pemKey = Base64.getDecoder().decode(pemKeyBase64)

        val pemObject = PEMParser(pemKey.inputStream().reader()).readObject() as? PEMKeyPair
                ?: throw IllegalStateException("edustor.accounts.jwk-key must be base64-encoded private PEM file")

        val converter = JcaPEMKeyConverter().setProvider("BC")
        Security.addProvider(BouncyCastleProvider())
        val keyPair = converter.getKeyPair(pemObject)

        signKey = keyPair.private
    }

    @RequestMapping
    fun token(@RequestParam payload: Map<String, String>): Map<String, Any> {
        val grantType = payload["grant_type"] ?: throw MissingArgumentException("grant_type")

        val (account, requestedScope) = when (grantType) {
            "password" -> processPasswordGrant(payload)
            "refresh_token" -> processRefreshTokenGrant(payload)
            else -> throw OAuthException("unsupported_grant_type", "$grantType is not recognized")
        }

        val scopeList = requestedScope.split(" ")
                .filter { it !in systemScopes }
        val scope = scopeList.joinToString(" ")

        val expiresInTime = when {
            "interactive" in scope -> 30 * 60
            else -> 10 * 60
        }

        val token = makeToken(account, scope, expiresInTime)

        val resp = mutableMapOf(
                "token" to token,
                "expires_in" to expiresInTime,
                "scope" to scope
        )

        if ("offline" in scopeList && grantType != "refresh_token") {
            val rt = RefreshToken(account = account, scope = requestedScope)
            refreshTokenRepository.save(rt)
            resp["refresh_token"] = rt.token
        }

        logger.info("Issuing token for ${account.id} with grant type $grantType and scope $scopeList")

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
        val rt = refreshTokenRepository.findOne(tokenStr) ?: throw InvalidGrantException("Invalid or expired refresh token")

        rt.lastUsed = Instant.now()
        refreshTokenRepository.save(rt)

        val account = rt.account
        return account to rt.scope
    }

    fun makeToken(account: Account, scope: String, expiresInTime: Int): String {
        try {
            val token = Jwts.builder()
                    .setSubject(account.id)
                    .claim("scope", scope)
                    .setExpiration(Date(System.currentTimeMillis() + expiresInTime * 1000))
                    .setIssuedAt(Date())
                    .signWith(SignatureAlgorithm.RS256, signKey)
                    .compact()
            return token
        } catch (e: Exception) {
            throw HttpRequestProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token")
        }
    }
}