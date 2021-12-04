package com.capstone.app.utrace_cts

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebasePushNotifService: FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        var title: String? = p0.notification?.title
        var content: String? = p0.notification?.body
        //TODO: Save the received message somewhere
    }
}