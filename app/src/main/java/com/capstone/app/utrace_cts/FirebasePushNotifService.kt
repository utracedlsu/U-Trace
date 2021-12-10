package com.capstone.app.utrace_cts

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.collections.HashMap

class FirebasePushNotifService: FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        var title: String? = p0.notification?.title
        var content: String? = p0.notification?.body
        var timeReceived = System.currentTimeMillis()

        Log.i("FirebaseNotifications", "New message received: ${title} - ${content}")
        //TODO: Save the received message somewhere
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