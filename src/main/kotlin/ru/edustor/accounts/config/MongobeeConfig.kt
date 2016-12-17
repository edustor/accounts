package ru.edustor.accounts.config

import com.github.mongobee.Mongobee
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
open class MongobeeConfig(
        val mongoTemplate: MongoTemplate) {
    @Bean
    open fun mongobee(): Mongobee {
        val uri = "mongodb://${mongoTemplate.db.mongo.address}/${mongoTemplate.db.name}"

        val runner = Mongobee(uri)
        runner.setChangeLogsScanPackage("ru.edustor.accounts.migration")
        runner.isEnabled = true

        return runner
    }
}