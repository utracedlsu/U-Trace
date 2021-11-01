package com.capstone.app.utrace_cts.status.persistence

import android.content.Context
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecordDatabase

//Calls statusDao
class StatusRecordStorage(val context: Context) {
    val statusDao = StreetPassRecordDatabase.getDatabase(context).statusDao()

    suspend fun saveRecord(record: StatusRecord) {
        statusDao.insert(record)
    }

    fun nukeDb() {
        statusDao.nukeDb()
    }

    fun getAllRecords(): List<StatusRecord> {
        return statusDao.getCurrentRecords()
    }

    suspend fun purgeOldRecords(before: Long) {
        statusDao.purgeOldRecords(before)
    }
}