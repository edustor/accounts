package ru.edustor.commons.exceptions.oauth2

class InvalidGrantException(description: String) : OAuthException("invalid_grant", description)