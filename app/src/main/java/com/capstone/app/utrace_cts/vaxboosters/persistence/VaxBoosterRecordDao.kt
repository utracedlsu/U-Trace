package com.capstone.app.utrace_cts.vaxboosters.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VaxBoosterRecordDao {
    @Query("SELECT * FROM vaxbooster_table ORDER BY id DESC")
    fun getLiveBoosters(): LiveData<List<VaxBoosterRecord>>

    @Query("SELECT * FROM vaxbooster_table ORDER BY id DESC")
    fun getCurrentBoosters(): List<VaxBoosterRecord>

    @Query("DELETE FROM vaxbooster_table")
    fun nukeDb()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: VaxBoosterRecord): Long
}