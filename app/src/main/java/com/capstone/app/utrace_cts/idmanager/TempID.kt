package com.capstone.app.utrace_cts.idmanager

import android.util.Log

class TempID(
    val startTime: Long,
    val tempID: String,
    val expiryTime: Long
) {

    fun isValidForCurrentTime(): Boolean {
        var currentTime = System.currentTimeMillis()
        return ((currentTime > (startTime * 1000)) && (currentTime < (expiryTime * 1000)))
    }

    fun print() {
        var tempIDStartTime = startTime * 1000
        var tempIDExpiryTime = expiryTime * 1000
        Log.d("TempID", "[TempID] Start time: ${tempIDStartTime}")
        Log.d("TempID","[TempID] Expiry time: ${tempIDExpiryTime}")
    }

    companion object {
        private const val TAG = "TempID"
    }
}