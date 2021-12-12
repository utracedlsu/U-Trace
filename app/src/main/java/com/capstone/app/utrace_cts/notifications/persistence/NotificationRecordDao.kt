package com.capstone.app.utrace_cts.notifications.persistence

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface NotificationRecordDao {
    @Query("SELECT * FROM notification_table ORDER BY timestamp DESC")
    fun getLiveNotifs(): LiveData<List<NotificationRecord>>

    @Query("SELECT * FROM notification_table ORDER BY timestamp DESC")
    fun getCurrentNotifs(): List<NotificationRecord>

    //May need to be changed to LiveData
    @Query("SELECT * FROM notification_table WHERE id = :id ORDER BY timestamp DESC LIMIT 1")
    fun getSingleNotif(id: String): NotificationRecord

    @Query("DELETE FROM notification_table")
    fun nukeDb()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(record: NotificationRecord): Long
}