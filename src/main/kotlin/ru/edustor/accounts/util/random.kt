package ru.edustor.accounts.util

import java.math.BigInteger
import java.security.SecureRandom

private val random = SecureRandom()

fun genRandomToken(bits: Int = 256): String {
    return BigInteger(bits, random).toString(32)
}