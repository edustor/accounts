package ru.edustor.accounts.exceptions.oauth2

class InvalidGrantException(description: String) : OAuthException("invalid_grant", description)