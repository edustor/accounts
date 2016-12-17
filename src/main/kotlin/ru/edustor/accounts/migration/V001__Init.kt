package ru.edustor.accounts.migration

import com.github.mongobee.changeset.ChangeLog
import com.github.mongobee.changeset.ChangeSet
import com.mongodb.*
import com.mongodb.util.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

@ChangeLog(order = "001")
class V001__Init {

    val logger: Logger = LoggerFactory.getLogger(V001__Init::class.java)

    @ChangeSet(author = "wutiarn", id = "001_init", order = "001")
    fun init(db: DB) {
        val jsonFile = File("migrations/001.json") // mongo-compatible arrray
        if (!jsonFile.exists()) {
            logger.warn("migrations/001.json not found. Skipping migration.")
        }

        val basicDBList = JSON.parse(jsonFile.readText()) as BasicDBList

        val collection = db.getCollection("account")
        basicDBList.forEach {
            collection.save(it as DBObject)
        }
    }
}