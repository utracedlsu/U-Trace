package com.capstone.app.utrace_cts

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.capstone.app.utrace_cts.BluetoothMonitoringService.Companion.PENDING_ACTIVITY
import com.capstone.app.utrace_cts.BluetoothMonitoringService.Companion.PENDING_WIZARD_REQ_CODE

class NotificationTemplates {
    companion object{

        fun getStartupNotification(context: Context, channel: String): Notification {

            val builder = NotificationCompat.Builder(context, channel)
                    .setContentText("U-Trace is setting up")
                    .setContentTitle("Setting things up")
                    .setOngoing(true)
                    //.setPriority(Notification.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.appiconnobg)
                    .setWhen(System.currentTimeMillis())
                    .setSound(null)
                    .setVibrate(null)
            return builder.build()
        }

        fun getRunningNotification(context: Context, channel: String): Notification{
            var intent = Intent(context, MainActivity::class.java)

            val activityPendingIntent = PendingIntent.getActivity(
                    context, PENDING_ACTIVITY,
                    intent, 0
            )

            val builder = NotificationCompat.Builder(context, channel)
                    .setContentTitle("U-Trace is currently scanning for close contacts!")
                    .setContentText("Reset your phone if this notification disappears.")
                    .setSmallIcon(R.drawable.appiconnobg)
                    .setContentIntent(activityPendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setSound(null)
                    .setVibrate(null)

            return builder.build()
        }

        fun lackingThingsNotification(context: Context, channel: String): Notification{
            //may need to change mainactivity to onboardingactivity
            var intent = Intent(context, MainActivity::class.java)
            intent.putExtra("page", 3)

            val activityPendingIntent = PendingIntent.getActivity(
                    context, PENDING_WIZARD_REQ_CODE,
                    intent, 0
            )

            val builder = NotificationCompat.Builder(context, channel)
                    .setContentTitle("Oh No!")
                    .setContentText("U-Trace is not scanning!")
                    .setOngoing(true)
                    .addAction(
                            R.drawable.appiconnobg,
                            "Open app now",
                            activityPendingIntent
                    )
                    .setSmallIcon(R.drawable.appiconnobg)
                    .setContentIntent(activityPendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setSound(null)
                    .setVibrate(null)

            return builder.build()
        }
    }
}