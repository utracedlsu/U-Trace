package com.capstone.app.utrace_cts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecord
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecordStorage
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecord
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecordStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
    A service for receiving firebase cloud messaging notifications
*/

class FirebasePushNotifService: FirebaseMessagingService() {
    private lateinit var notificationRecordStorage: NotificationRecordStorage
    private lateinit var vaxBoosterRecordStorage: VaxBoosterRecordStorage

    override fun onCreate() {
        super.onCreate()
        Log.i("FirebaseNotifications", "OnCreate - FirebaseNotifications")
        setup()
    }

    fun setup(){
        notificationRecordStorage = NotificationRecordStorage(this.applicationContext)
        vaxBoosterRecordStorage = VaxBoosterRecordStorage(this.applicationContext)
        Log.i("FirebaseNotifications", "setup() - FirebaseNotifications")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        //do not use notifications key for now, instead put everything at data key
        var dataNotifTitle = p0.data["title"]
        var dataNotifContent = p0.data["body"]
        var notifFlag = p0.data["notif_flag"]

        Log.i("FirebaseNotifications", "New message received: ${dataNotifTitle} - ${dataNotifContent}")
        Log.i("FirebaseNotifications", "Notification flag: $notifFlag")
        Log.i("FirebaseNotifications", "Saving notif to db")
        val notifRecord = NotificationRecord(
            title = dataNotifTitle.toString(),
            body = dataNotifContent.toString()
        )

        /*
            Based on the notification flag, we will retrieve certain user data and save it to the device's preferences
            1 - Covid Test Result (Save in prefs)
            2 - Vaccine Status Update (1st Dose) (Save in prefs)
            3 - Vaccine Booster Shot
            4 - Close Contact (?)
        */
        val firebaseUserID = Preference.getFirebaseId(applicationContext)
        Log.i("FirebaseNotifications", "Firebase ID: $firebaseUserID")
        when(notifFlag){
            "1" -> { //get latest test info only
                Log.i("FirebaseNotifications", "Attempting to retrieve test data...")
                FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                    .get().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Log.i("FirebaseNotifications", "Task successful, saving to preferences")
                            val snapshot = task.result
                            val latestTestResult = snapshot?.getBoolean("covid_positive")
                            val latestTestDate = snapshot?.getString("last_testdate")
                            val covidTests = snapshot?.get("covid_tests") as ArrayList<HashMap<String, Object>>
                            val latestTestID = covidTests.last().get("testID")

                            val retrievedFac = covidTests.last().get("facility")
                            val retrievedMeth = covidTests.last().get("method")

                            if(retrievedFac != null && retrievedMeth != null){
                                Preference.putLastTestFac(applicationContext, retrievedFac.toString())
                                Preference.putLastTestMethod(applicationContext, retrievedMeth.toString())
                            }

                            //save latest test data to preferences
                            Preference.putTestStatus(applicationContext, latestTestResult.toString())
                            Preference.putLastTestDate(applicationContext, latestTestDate.toString())
                            Preference.putLastTestID(applicationContext, latestTestID.toString())
                            Log.i("FirebaseNotifications", "Test Results have been updated")

                        } else {
                            Log.e("FirebaseNotifications", "Failed to get Test Data: ${task.exception?.message}")
                        }
                    }
            }
            "2" -> { //get vaccine info (id, 1st/2nd dose, manufacturer)
                Log.i("FirebaseNotifications", "Attempting to retrieve vaccine data...")
                FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                    .get().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Log.i("FirebaseNotifications", "Task successful, saving to preferences")
                            val snapshot = task.result

                            val vaxID = snapshot?.getString("vax_ID")
                            val vaxManufacturer = snapshot?.getString("vax_manufacturer")
                            val vaxCategory = snapshot?.getString("vax_category")
                            val vaxFacility = snapshot?.getString("vax_facility")

                            //save latest vax data to preferences
                            Preference.putVaxID(applicationContext, vaxID.toString())
                            Preference.putVaxManufacturer(applicationContext, vaxManufacturer.toString())
                            Preference.putVaxCategory(applicationContext, vaxCategory.toString())
                            Preference.putVaxFacility(applicationContext, vaxFacility.toString())

                            // 5/13/2022 - Vaccination Data Redesign
                            val vax1Map = snapshot?.get("vax_1stdose") as HashMap<String, Object>
                            if(!vax1Map.isEmpty()){
                                Preference.putVaxDose(applicationContext, "${vax1Map.get("date").toString()}", 1)
                                Preference.putVaxLotNo(applicationContext, "${vax1Map.get("lot_no").toString()}", 1)
                                Preference.putVaxBatchNo(applicationContext, "${vax1Map.get("batch_no").toString()}", 1)
                                Preference.putVaxVaccinator(applicationContext, "${vax1Map.get("vaccinator").toString()}", 1)
                            }

                            val vax2Map = snapshot?.get("vax_2nddose") as HashMap<String, Object>
                            if(!vax2Map.isEmpty()){
                                Preference.putVaxDose(applicationContext, "${vax2Map.get("date").toString()}", 2)
                                Preference.putVaxLotNo(applicationContext, "${vax2Map.get("lot_no").toString()}", 2)
                                Preference.putVaxBatchNo(applicationContext, "${vax2Map.get("batch_no").toString()}", 2)
                                Preference.putVaxVaccinator(applicationContext, "${vax2Map.get("vaccinator").toString()}", 2)
                            }

                            Log.i("FirebaseNotifications", "Vaccination data has been updated")

                            val boostersArray = snapshot?.get("vax_booster") as ArrayList<HashMap<String, Object>>
                            var sqliteBoosters = ArrayList<VaxBoosterRecord>()

                            if(boostersArray.size > 0){
                                for (fsBooster in boostersArray){
                                    sqliteBoosters.add(VaxBoosterRecord(
                                        vaxbrand = fsBooster.get("vax_manufacturer").toString(),
                                        date = fsBooster.get("date").toString(),
                                        blockno = fsBooster.get("batch_no").toString(),
                                        lotno = fsBooster.get("lot_no").toString(),
                                        vaccinator = fsBooster.get("vaccinator").toString(),
                                        category = fsBooster.get("category").toString(),
                                        facility = fsBooster.get("facility").toString()
                                    ))
                                }

                                GlobalScope.launch {
                                    vaxBoosterRecordStorage.nukeDb()
                                    vaxBoosterRecordStorage.saveMultipleBoosters(sqliteBoosters)
                                }
                            }

                        } else {
                            Log.e("FirebaseNotifications", "Failed to get vaccine data: ${task.exception?.message}")
                        }
                    }
            }
            "3" -> { //get LATEST booster data (can be recustomized to get all data instead just in case)
                Log.i("FirebaseNotifications", "Attempting to retrieve booster data...")
                FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                    .get().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Log.i("FirebaseNotifications", "Task successful, saving to database")
                            val snapshot = task.result

                            val vaxID = snapshot?.getString("vax_ID")
                            val vaxManufacturer = snapshot?.getString("vax_manufacturer")
                            val vaxCategory = snapshot?.getString("vax_category")
                            val vaxFacility = snapshot?.getString("vax_facility")

                            Preference.putVaxID(applicationContext, vaxID.toString())
                            Preference.putVaxManufacturer(applicationContext, vaxManufacturer.toString())
                            Preference.putVaxCategory(applicationContext, vaxCategory.toString())
                            Preference.putVaxFacility(applicationContext, vaxFacility.toString())

                            // 5/13/2022 - Vaccination Data Redesign
                            val vax1Map = snapshot?.get("vax_1stdose") as HashMap<String, Object>
                            if(!vax1Map.isEmpty()){
                                Preference.putVaxDose(applicationContext, "${vax1Map.get("date").toString()}", 1)
                                Preference.putVaxLotNo(applicationContext, "${vax1Map.get("lot_no").toString()}", 1)
                                Preference.putVaxBatchNo(applicationContext, "${vax1Map.get("batch_no").toString()}", 1)
                                Preference.putVaxVaccinator(applicationContext, "${vax1Map.get("vaccinator").toString()}", 1)
                            }

                            val vax2Map = snapshot?.get("vax_2nddose") as HashMap<String, Object>
                            if(!vax2Map.isEmpty()){
                                Preference.putVaxDose(applicationContext, "${vax2Map.get("date").toString()}", 2)
                                Preference.putVaxLotNo(applicationContext, "${vax2Map.get("lot_no").toString()}", 2)
                                Preference.putVaxBatchNo(applicationContext, "${vax2Map.get("batch_no").toString()}", 2)
                                Preference.putVaxVaccinator(applicationContext, "${vax2Map.get("vaccinator").toString()}", 2)
                            }

                            val boostersArray = snapshot?.get("vax_booster") as ArrayList<HashMap<String, Object>>
                            var sqliteBoosters = ArrayList<VaxBoosterRecord>()

                            for (fsBooster in boostersArray){
                                sqliteBoosters.add(VaxBoosterRecord(
                                    vaxbrand = fsBooster.get("vax_manufacturer").toString(),
                                    date = fsBooster.get("date").toString(),
                                    blockno = fsBooster.get("batch_no").toString(),
                                    lotno = fsBooster.get("lot_no").toString(),
                                    vaccinator = fsBooster.get("vaccinator").toString(),
                                    category = fsBooster.get("category").toString(),
                                    facility = fsBooster.get("facility").toString()
                                ))
                            }

                            GlobalScope.launch {
                                vaxBoosterRecordStorage.nukeDb()
                                vaxBoosterRecordStorage.saveMultipleBoosters(sqliteBoosters)
                            }

                            Log.i("FirebaseNotifications", "Vaccination booster data has been updated")

                        } else {
                            Log.e("FirebaseNotifications", "Failed to get booster data: ${task.exception?.message}")
                        }
                    }
            }
            else -> {
                Log.i("FirebaseNotifications", "Don't do anything with received flag: $notifFlag")
            }
        }

        //notify user
        sendNotification(dataNotifTitle.toString(), dataNotifContent.toString())

        //save notification to local database
        notificationRecordStorage.saveNotif(notifRecord)
    }

    //Activates when the app is first installed
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        Log.i("FirebaseNotifications", "New cloud messaging token: $p0")
        Preference.putCloudMessagingToken(applicationContext, p0)
    }

    //instead of using the 'notifications' JSON key, we will build the notification in the app side instead
    private fun sendNotification(title: String, body: String){
        Log.i("FirebaseNotifications", "Building Notifications...")
        val intent = Intent(this, MainActivity::class.java)

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_ONE_SHOT
        )

        //set up notifs n stuff
        val mNotifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val mChannel = NotificationChannel(BluetoothMonitoringService.PUSH_NOTIFICATION_CHANNEL_NAME,
                BluetoothMonitoringService.PUSH_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            mChannel.enableLights(false)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(0L)
            mChannel.setSound(null, null)
            mChannel.setShowBadge(false)
            mNotifManager!!.createNotificationChannel(mChannel)
        }

        val fcmNotifBuilder = NotificationCompat.Builder(this, BluetoothMonitoringService.PUSH_NOTIFICATION_CHANNEL_NAME)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.appiconnobg)
            .setContentIntent(activityPendingIntent)
            .setWhen(System.currentTimeMillis())
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        mNotifManager.notify(BluetoothMonitoringService.PUSH_NOTIFICATION_ID, fcmNotifBuilder.build())
    }
}