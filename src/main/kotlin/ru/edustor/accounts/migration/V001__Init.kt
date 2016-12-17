package ru.edustor.accounts.migration

import com.github.mongobee.changeset.ChangeLog
import com.github.mongobee.changeset.ChangeSet
import com.mongodb.DB

@ChangeLog(order = "001")
class V001__Init() {
    @ChangeSet(author = "wutiarn", id = "001_init", order = "001")
    fun init(db: DB) {

    }
}