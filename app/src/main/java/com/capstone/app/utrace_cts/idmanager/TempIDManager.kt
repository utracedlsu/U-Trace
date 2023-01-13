package com.capstone.app.utrace_cts.idmanager

import android.content.Context
import android.util.Log
import com.capstone.app.utrace_cts.BluetoothMonitoringService.Companion.bmValidityCheck
import com.capstone.app.utrace_cts.Preference
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*

object TempIDManager {
    fun storeTemporaryIDs(context: Context, packet: String) {
        Log.d("TempIDManager", "[TempID] Storing temporary IDs into internal storage...")
        val file = File(context.filesDir, "tempIDs")
        file.writeText(packet)
    }

    fun retrieveTemporaryID(context: Context): TempID? {
        val file = File(context.filesDir, "tempIDs")
        if (file.exists()) {
            val readback = file.readText()
            Log.d("TempIDManager", "[TempID] fetched broadcastmessage from file:  $readback")
            var tempIDArrayList =
                convertToTemporaryIDs(
                    readback
                )
            var tempIDQueue =
                convertToQueue(
                    tempIDArrayList
                )
            return getValidOrLastTemporaryID(
                context,
                tempIDQueue
            )
        }
        return null
    }

    private fun getValidOrLastTemporaryID(
        context: Context,
        tempIDQueue: Queue<TempID>
    ): TempID {
        Log.d("TempIDManager", "[TempID] Retrieving Temporary ID")
        var currentTime = System.currentTimeMillis()

        var pop = 0
        while (tempIDQueue.size > 1) {
            val tempID = tempIDQueue.peek()
            tempID.print()

            if (tempID.isValidForCurrentTime()) {
                Log.d("TempIDManager", "[TempID] Breaking out of the loop")
                break
            }

            tempIDQueue.poll()
            pop++
        }

        var foundTempID = tempIDQueue.peek()
        var foundTempIDStartTime = foundTempID.startTime * 1000
        var foundTempIDExpiryTime = foundTempID.expiryTime * 1000

        Log.d("TempIDManager", "[TempID Total number of items in queue: ${tempIDQueue.size}")
        Log.d("TempIDManager", "[TempID Number of items popped from queue: $pop")
        Log.d("TempIDManager", "[TempID] Current time: ${currentTime}")
        Log.d("TempIDManager", "[TempID] Start time: ${foundTempIDStartTime}")
        Log.d("TempIDManager", "[TempID] Expiry time: ${foundTempIDExpiryTime}")
        Log.d("TempIDManager", "[TempID] Updating expiry time")
        Preference.putExpiryTimeInMillis(
            context,
            foundTempIDExpiryTime
        )
        return foundTempID
    }

    private fun convertToTemporaryIDs(tempIDString: String): Array<TempID> {
        val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

        val tempIDResult = gson.fromJson(tempIDString, Array<TempID>::class.java)
        Log.d(
            "TempIDManager",
            "[TempID] After GSON conversion: ${tempIDResult[0].tempID} ${tempIDResult[0].startTime}"
        )

        return tempIDResult
    }

    private fun convertToQueue(tempIDArray: Array<TempID>): Queue<TempID> {
        Log.d("TempIDManager", "[TempID] Before Sort: ${tempIDArray[0]}")

        //Sort based on start time
        tempIDArray.sortBy {
            return@sortBy it.startTime
        }
        Log.d("TempIDManager", "[TempID] After Sort: ${tempIDArray[0]}")

        //Preserving order of array which was sorted
        var tempIDQueue: Queue<TempID> = LinkedList<TempID>()
        for (tempID in tempIDArray) {
            tempIDQueue.offer(tempID)
        }

        Log.d("TempIDManager", "[TempID] Retrieving from Queue: ${tempIDQueue.peek()}")
        return tempIDQueue
    }

    fun getTemporaryIDs(context: Context, functions: FirebaseFunctions): Task<HttpsCallableResult> {
        return functions.getHttpsCallable("getTempIDs").call().addOnSuccessListener {
            val result: HashMap<String, Any> = it.data as HashMap<String, Any>
            val tempIDs = result["tempIDs"]

            val status = result["status"].toString()
            if (status.toLowerCase().contentEquals("success")) {
                Log.w("TempIDManager", "Retrieved Temporary IDs from Server")
                val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
                val jsonByteArray = gson.toJson(tempIDs).toByteArray(Charsets.UTF_8)
                storeTemporaryIDs(
                    context,
                    jsonByteArray.toString(Charsets.UTF_8)
                )

                val refreshTime = result["refreshTime"].toString()
                var refresh = refreshTime.toLongOrNull() ?: 0
                Preference.putNextFetchTimeInMillis(
                    context,
                    refresh * 1000
                )
                Preference.putLastFetchTimeInMillis(
                    context,
                    System.currentTimeMillis() * 1000
                )
            }

        }.addOnFailureListener {
            Log.d("TempIDManager", "[TempID] Error getting Temporary IDs")
        }
    }

    fun needToUpdate(context: Context): Boolean {
        val nextFetchTime =
            Preference.getNextFetchTimeInMillis(context)
        val currentTime = System.currentTimeMillis()

        val update = currentTime >= nextFetchTime
        Log.i("TempIDManager",
            "Need to update and fetch TemporaryIDs? $nextFetchTime vs $currentTime: $update"
        )
        return update
    }

    fun needToRollNewTempID(context: Context): Boolean {
        val expiryTime =
            Preference.getExpiryTimeInMillis(context)
        val currentTime = System.currentTimeMillis()
        val update = currentTime >= expiryTime
        Log.d("TempIDManager", "[TempID] Need to get new TempID? $expiryTime vs $currentTime: $update")
        return update
    }

    fun bmValid(context: Context): Boolean {
        val expiryTime =
            Preference.getExpiryTimeInMillis(context)
        val currentTime = System.currentTimeMillis()
        val update = currentTime < expiryTime

        if (bmValidityCheck) {
            Log.w("TempIDManager", "Temp ID is valid")
            return update
        }

        return true
    }
}