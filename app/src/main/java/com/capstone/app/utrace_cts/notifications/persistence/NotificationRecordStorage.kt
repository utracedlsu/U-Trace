package com.capstone.app.utrace_cts.notifications.persistence

import android.content.Context
import android.util.Log

class NotificationRecordStorage(val context: Context) {
    val notifDao = NotificationRecordDatabase.getDatabase(context).notifDao()

    fun saveNotif(notif: NotificationRecord){
        notifDao.insert(notif)
        Log.i("NotifRecords", "Notification record saved")
    }

    fun deleteSingleNotif(id: Int){
        notifDao.deleteSingleNotif(id)
    }

    fun nukeDb(){
        notifDao.nukeDb()
    }

    fun getAllNotifs(): List<NotificationRecord>{
        return notifDao.getCurrentNotifs()
    }
}