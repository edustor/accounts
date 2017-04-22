package ru.edustor.accounts.exceptions.oauth2

import org.springframework.http.HttpStatus

class RegistrationDisabledException(description: String) : OAuthException("registration_disabled", description, status = HttpStatus.FORBIDDEN)