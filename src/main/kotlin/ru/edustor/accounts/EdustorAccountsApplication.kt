package ru.edustor.accounts

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = arrayOf("ru.edustor"))
open class EdustorAccountsApplication {
    companion object {
        val VERSION: String = "0.6.0a1"
    }
}