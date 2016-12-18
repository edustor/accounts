package ru.edustor.accounts.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component
@PropertySource("classpath:build.properties")
open class EdustorVersionInfoHolder(
        @Value("\${edustor.build.version}") val version: String,
        @Value("\${edustor.build.commitId}") val commitId: String
)