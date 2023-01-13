package com.capstone.app.utrace_cts.notifications.persistence

import androidx.lifecycle.LiveData

class NotificationRecordRepository (private val notifDao: NotificationRecordDao) {
    val allNotifs: LiveData<List<NotificationRecord>> = notifDao.getLiveNotifs()

    fun insert(notif: NotificationRecord){
        notifDao.insert(notif)
    }
}