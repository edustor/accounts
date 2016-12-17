package ru.edustor.accounts.migration

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mongobee.changeset.ChangeLog
import com.github.mongobee.changeset.ChangeSet
import com.mongodb.BasicDBObject
import com.mongodb.DB
import org.slf4j.LoggerFactory
import java.io.File

@ChangeLog(order = "001")
class V001__Init {

    val logger = LoggerFactory.getLogger(V001__Init::class.java)

    @ChangeSet(author = "wutiarn", id = "001_init", order = "001")
    fun init(db: DB) {
        val jsonFile = File("migrations/001.json")
        if (!jsonFile.exists()) {
            logger.warn("migrations/001.json not found. Skipping migration.")
        }


        val objectMapper = ObjectMapper()
        val accounts = objectMapper.readTree(jsonFile)
//        val accounts = objectMapper.readValue<List<Account>>(jsonFile, object : TypeReference<List<Account>>() {})

        val collection = db.getCollection("account")
        val bsonList = accounts.map { account ->
            val bsonObject = BasicDBObject()
            account.fieldNames().forEach { key ->
                bsonObject.put(key, account[key].textValue())
            }
            return@map bsonObject
        }

        collection.insert(bsonList)
    }
}