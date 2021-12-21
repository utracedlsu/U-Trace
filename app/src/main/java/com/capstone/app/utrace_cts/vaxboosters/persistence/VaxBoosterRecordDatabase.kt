package com.capstone.app.utrace_cts.vaxboosters.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(VaxBoosterRecord::class),
    version = 1
)
abstract class VaxBoosterRecordDatabase: RoomDatabase() {
    abstract fun vaxBoosterDao(): VaxBoosterRecordDao

    companion object {
        @Volatile
        private var INSTANCE: VaxBoosterRecordDatabase? = null

        fun getDatabase(context: Context): VaxBoosterRecordDatabase{
            val tempInstance = VaxBoosterRecordDatabase.INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    VaxBoosterRecordDatabase::class.java,
                    "vaxbooster_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}