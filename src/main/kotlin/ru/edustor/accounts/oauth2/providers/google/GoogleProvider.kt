package ru.edustor.accounts.oauth2.providers.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.stereotype.Component
import ru.edustor.accounts.model.Account
import ru.edustor.accounts.repository.AccountRepository
import ru.edustor.accounts.exceptions.oauth2.InvalidGrantException

@Component
open class GoogleProvider(val accountRepository: AccountRepository) {

    private val AUDIENCE = "99685742253-41uieqd0vl3e03l62c7t3impd38gdt4q.apps.googleusercontent.com"

    private val mobileVerifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory())
            .setAudience(listOf(AUDIENCE))
            .setIssuer("https://accounts.google.com")
            .build()

    private val webVerifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory())
            .setAudience(listOf(AUDIENCE))
            .setIssuer("https://accounts.google.com")
            .build()

    fun processIdToken(token: String): GoogleAccount {
        val googleId: GoogleIdToken
        try {
            googleId = mobileVerifier.verify(token) ?: webVerifier.verify(token)
        } catch (e: Throwable) {
            throw throw InvalidGrantException("Google ID token parse error")
        }
        googleId ?: throw InvalidGrantException("Bad Google ID token")
        
        val payload = googleId.payload
        val googleAccount = GoogleAccount(
                payload["sub"] as String,
                payload["name"] as String?,
                payload["given_name"] as String?,
                payload["family_name"] as String?,
                payload["email"] as String?,
                payload.getOrElse("email_verified", { false }) as Boolean,
                payload["locale"] as String? ,
                payload["picture"] as String?
        )

        return googleAccount
    }

    fun authenticateWithGoogle(idToken: String): Account {
        val googleAccount = processIdToken(idToken)

        val account = accountRepository.findByGoogleSub(googleAccount.sub) ?: let {
            val account = Account()
            account.googleSub = googleAccount.sub
            account.email = googleAccount.email
            accountRepository.save(account)
            account
        }

        return account
    }
}
