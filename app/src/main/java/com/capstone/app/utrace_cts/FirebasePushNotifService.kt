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
        var notifFlag = p0.data["notif_flag"]

        Log.i("FirebaseNotifications", "New message received: ${recTitle} - ${recContent}")
        Log.i("FirebaseNotifications", "Notification flag: $notifFlag")
        Log.i("FirebaseNotifications", "Saving notif to db")
        val notifRecord = NotificationRecord(
            title = recTitle.toString(),
            body = recContent.toString()
        )

        //TODO: check flag of notification
        /*
            1 - Covid Test Result (Save in prefs)
            2 - Close Contact
            3 - Vaccine Status Update (1st Dose) (Save in prefs)
            4 - Vaccine Status Update (2nd Dose) (Save in prefs)
            5 - Vaccine Booster Shot
        */
        val firebaseUserID = Preference.getFirebaseId(applicationContext)
        Log.i("FirebaseNotifications", "Firebase ID: $firebaseUserID")
        when(notifFlag){
            "1" -> {
                Log.i("FirebaseNotifications", "Attempting to retrieve test data...")
                FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                    .get().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Log.i("FirebaseNotifications", "Task successful, saving to preferences")
                            val snapshot = task.result
                            val latestTestResult = snapshot?.getBoolean("covid_positive")
                            val latestTestDate = snapshot?.getString("last_testdate")
                            //comment
                            //save latest test data to preferences
                            Preference.putTestStatus(applicationContext, latestTestResult.toString())
                            Preference.putLastTestDate(applicationContext, latestTestDate.toString())
                            Log.i("FirebaseNotifications", "Test Results have been updated")

                        } else {
                            Log.e("FirebaseNotifications", "Failed to get Test Data: ${task.exception?.message}")
                        }
                    }
            }
        }


        //save notification to local database
        notificationRecordStorage.saveNotif(notifRecord)
    }

    //Activates when the app is first installed (?)
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        var fUserId = FirebaseAuth.getInstance().currentUser?.uid

        Log.i("FirebaseNotifications", "New cloud messaging token: $p0")
        Preference.putCloudMessagingToken(applicationContext, p0)
    }
}