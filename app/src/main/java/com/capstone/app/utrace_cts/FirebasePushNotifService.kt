package com.capstone.app.utrace_cts

import android.util.Log
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecord
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecordStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.collections.HashMap

class FirebasePushNotifService: FirebaseMessagingService() {
    private lateinit var notificationRecordStorage: NotificationRecordStorage

    override fun onCreate() {
        super.onCreate()
        Log.i("FirebaseNotifications", "OnCreate - FirebaseNotifications")
        setup()
    }

    fun setup(){
        notificationRecordStorage = NotificationRecordStorage(this.applicationContext)
        Log.i("FirebaseNotifications", "setup() - FirebaseNotifications")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        var recTitle: String? = p0.notification?.title
        var recContent: String? = p0.notification?.body

        Log.i("FirebaseNotifications", "New message received: ${recTitle} - ${recContent}")
        Log.i("FirebaseNotifications", "Saving notif to db")
        val notifRecord = NotificationRecord(
            title = recTitle.toString(),
            body = recContent.toString()
        )
        notificationRecordStorage.saveNotif(notifRecord)
    }

    //Activates when the app is first installed (?)
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        var fUserId = FirebaseAuth.getInstance().currentUser?.uid

        Log.i("FirebaseNotifications", "New cloud messaging token: $p0")
        Preference.putCloudMessagingToken(applicationContext, p0)

        /*
        FirebaseFirestore.getInstance().collection("users")
            .document(fUserId.toString()).update("fcm_token", p0).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.i("FirebaseNotifications", "Successfully sent token to server")
                } else {
                    Log.e("FirebaseNotifications", "Unable to send token to server: ${task.exception?.message}")
                }
            }
        */
    }
}