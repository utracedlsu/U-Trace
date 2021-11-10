package com.capstone.app.utrace_cts

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import com.capstone.app.utrace_cts.idmanager.TempIDManager

class TracerApp: Application() {
    override fun onCreate() {
        super.onCreate()

    }
    companion object {

        lateinit var AppContext: Context

        fun thisDeviceMsg(): String {
            BluetoothMonitoringService.broadcastMessage?.let {
                Log.i("TracerApp", "Retrieved BM for storage: $it")

                if (!it.isValidForCurrentTime()) {

                    var fetch = TempIDManager.retrieveTemporaryID(AppContext)
                    fetch?.let {
                        Log.i("TracerApp", "Grab New Temp ID")
                        BluetoothMonitoringService.broadcastMessage = it
                    }

                    if (fetch == null) {
                        Log.i("TracerApp", "Failed to grab new Temp ID")
                    }

                }
            }
            return BluetoothMonitoringService.broadcastMessage?.tempID ?: "Missing TempID"
        }
    }
}