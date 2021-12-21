package com.capstone.app.utrace_cts.vaxboosters.persistence

import android.content.Context
import android.util.Log
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecordDatabase

class VaxBoosterRecordStorage (val context: Context) {
    val boosterDao = NotificationRecordDatabase.getDatabase(context).vaxDao()

    suspend fun saveBooster (booster: VaxBoosterRecord){
        boosterDao.insert(booster)
        Log.i("BoosterRecords", "Vax Booster Saved")
    }

    fun nukeDb(){
        boosterDao.nukeDb()
    }

    fun getAllBoosters(): List<VaxBoosterRecord>{
        return boosterDao.getCurrentBoosters()
    }
}