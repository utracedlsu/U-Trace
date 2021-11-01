package com.capstone.app.utrace_cts.streetpass.persistence

import android.content.Context
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecord
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecordDatabase

//
class StreetPassRecordStorage (val context: Context){
    val recordDao = StreetPassRecordDatabase.getDatabase(context).recordDao()

    suspend fun saveRecord(record: StreetPassRecord) {
        recordDao.insert(record)
    }

    fun nukeDb() {
        recordDao.nukeDb()
    }

    fun getAllRecords(): List<StreetPassRecord> {
        return recordDao.getCurrentRecords()
    }

    suspend fun purgeOldRecords(before: Long) {
        recordDao.purgeOldRecords(before)
    }
}