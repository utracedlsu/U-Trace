package com.capstone.app.utrace_cts.Streetpass

import android.content.Context
import com.capstone.app.utrace_cts.Bluetooth.GattServer
import com.capstone.app.utrace_cts.Bluetooth.GattService

class StreetPassServer constructor(val context: Context, val serviceUUIDString: String) {

    private var gattServer: GattServer?= null

    init {
        gattServer = setupGattServer(context, serviceUUIDString)
    }

    private fun setupGattServer(context: Context, serviceUUIDString: String): GattServer?{
        val gattServer = GattServer(context, serviceUUIDString)
        var started = gattServer.startServer()

        if(started){
            val readService = GattService(context, serviceUUIDString)
            gattServer.addService(readService)
            return gattServer
        }
        return null
    }

     fun tearDown(){
         gattServer?.stop()
     }

     fun checkServiceAvailable(): Boolean{
         return gattServer?.bluetoothGattServer?.services?.filter{
             it.uuid.toString().equals(serviceUUIDString)
         }?.isNotEmpty() ?: false
     }
}