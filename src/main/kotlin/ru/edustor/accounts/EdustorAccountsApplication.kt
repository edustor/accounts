package ru.edustor.accounts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.util.*

@SpringBootApplication
@EntityScan(basePackageClasses = arrayOf(EdustorAccountsApplication::class, Jsr310JpaConverters::class))
open class EdustorAccountsApplication : WebMvcConfigurerAdapter() {
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedOrigins("*")
    }
}