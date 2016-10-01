package ru.edustor.commons.exceptions.oauth2

class MissingArgumentException(argument: String) : OAuthException("invalid_request", "$argument was not provided")