package com.capstone.app.utrace_cts.streetpass.persistence

import androidx.lifecycle.LiveData
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecord
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecordDao

//Comment
class StreetPassRecordRepository (private val recordDao: StreetPassRecordDao) {
    val allRecords: LiveData<List<StreetPassRecord>> = recordDao.getRecords()

    suspend fun insert(word: StreetPassRecord) {
        recordDao.insert(word)
    }
}