package com.capstone.app.utrace_cts.idmanager

import android.content.Context
import android.util.Log
import com.capstone.app.utrace_cts.Preference
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
}