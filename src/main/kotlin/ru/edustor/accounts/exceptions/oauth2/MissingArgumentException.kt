package ru.edustor.accounts.exceptions.oauth2

class MissingArgumentException(argument: String) : OAuthException("invalid_request", "$argument was not provided")