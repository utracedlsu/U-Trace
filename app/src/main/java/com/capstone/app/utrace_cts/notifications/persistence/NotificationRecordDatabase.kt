package com.capstone.app.utrace_cts.notifications.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = arrayOf(NotificationRecord::class),
    version = 1
)
abstract class NotificationRecordDatabase: RoomDatabase() {
    abstract fun notifDao(): NotificationRecordDao

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