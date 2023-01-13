package com.capstone.app.utrace_cts.notifications.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecord
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecordDao


@Database(
    entities = arrayOf(NotificationRecord::class, VaxBoosterRecord::class),
    version = 1
)
abstract class NotificationRecordDatabase: RoomDatabase() {
    abstract fun notifDao(): NotificationRecordDao
    abstract fun vaxDao(): VaxBoosterRecordDao

    companion object {
        @Volatile
        private var INSTANCE: NotificationRecordDatabase? = null

        fun getDatabase(context: Context): NotificationRecordDatabase {
            val tempInstance = NotificationRecordDatabase.INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    NotificationRecordDatabase::class.java,
                    "notification_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}