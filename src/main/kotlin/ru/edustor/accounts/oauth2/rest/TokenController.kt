package ru.edustor.accounts.oauth2.rest

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
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
import ru.edustor.accounts.repository.AccountRepository
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.*

@RestController
@RequestMapping(value = "/oauth2/token", method = arrayOf(RequestMethod.POST))
class TokenController(
        val googleProvider: GoogleProvider,
        val accountRepository: AccountRepository,
        environment: Environment) {

//    Use "openssl pkcs8 -topk8 -inform PEM -outform DER -in CA_key.pem -out CA_key.der -nocrypt" to convert PEM key
    val jwkKeyBase64: String = environment.getRequiredProperty("edustor.accounts.jwk-key")
    val logger: Logger = LoggerFactory.getLogger(TokenController::class.java)
    val TOKEN_EXPIRE_IN = 10 * 60 // Seconds
    val signKey: PrivateKey
    val systemScopes = arrayOf("internal")

    init {
        val keyBytes = Base64.getDecoder().decode(jwkKeyBase64)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        signKey = KeyFactory.getInstance("RSA").generatePrivate(spec)
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

        val token = makeToken(account, scope)

        val resp = mutableMapOf(
                "token" to token,
                "expires_in" to TOKEN_EXPIRE_IN,
                "scope" to scope
        )

        if ("offline" in scopeList && grantType != "refresh_token") {
            val rt = RefreshToken(requestedScope)
            account.refreshTokens.add(rt)
            accountRepository.save(account)
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
        val account = accountRepository.findByRefreshTokensTokenIn(tokenStr) ?: throw InvalidGrantException("Invalid or expired refresh token")
        val token = account.refreshTokens.first { it.token == tokenStr }
        token.lastUsed = Instant.now()
        accountRepository.save(account)
        return account to token.scope
    }

    fun makeToken(account: Account, scope: String): String {
        try {
            val token = Jwts.builder()
                    .setSubject(account.id)
                    .claim("scope", scope)
                    .setExpiration(Date(System.currentTimeMillis() + TOKEN_EXPIRE_IN * 1000))
                    .setIssuedAt(Date())
                    .signWith(SignatureAlgorithm.RS256, signKey)
                    .compact()
            return token
        } catch (e: Exception) {
            throw HttpRequestProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token")
        }
    }
}