package ru.edustor.accounts.oauth2.providers.google

data class GoogleAccount(
        val sub: String,
        val name: String?,
        val given_name: String?,
        val family_name: String?,
        val email: String?,
        val emailVerified: Boolean,
        val locale: String?,
        val picture: String?
)