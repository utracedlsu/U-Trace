package com.capstone.app.utrace_cts

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.capstone.app.utrace_cts.streetpass.StreetPassWorker
import kotlin.properties.Delegates

class Work constructor(
    var device: BluetoothDevice,
    var connectable: ConnectablePeripheral,
    private val onWorkTimeoutListener : OnWorkTimeoutListener
): Comparable<Work>{
    var timeStamp: Long by Delegates.notNull()
    var checklist = WorkCheckList()
    var gatt: BluetoothGatt? = null
    var finished = false
    var timeout: Long = 0

    val timeoutRunnable: Runnable = Runnable {
        onWorkTimeoutListener.onWorkTimeout(this)
    }

    init {
        timeStamp = System.currentTimeMillis()
    }

    fun isSafelyCompleted(): Boolean{
        return (checklist.connected.status && checklist.readCharacteristic.status &&
                checklist.writeCharacteristic.status && checklist.disconnected.status)
                || checklist.skipped.status
    }
    fun isCriticalsCompleted(): Boolean {
        return (checklist.connected.status && checklist.readCharacteristic.status &&
                checklist.writeCharacteristic.status) || checklist.skipped.status
    }

    fun startWork(
            context: Context,
            /* TODO: Parameter should changed to Worker.CentralGattCallback */
            gattCallback: StreetPassWorker.CentralGattCallback
    ){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        }else{
            gatt = device.connectGatt(context, false, gattCallback)
        }

        if(gatt == null){
            Log.w("Work", "Unable to connect to ${device.address}")
        }
    }

    override fun compareTo(other: Work): Int {
        return timeStamp.compareTo(other.timeStamp)
    }

    inner class WorkCheckList {
        var started = Check()
        var connected = Check()
        var mtuChanged = Check()
        var readCharacteristic = Check()
        var writeCharacteristic = Check()
        var disconnected = Check()
        var skipped = Check()

        override fun toString(): String {
            return Gson().toJson(this)
        }
    }

    inner class Check {
        var status = false
        var timePerformed: Long = 0
    }

    interface OnWorkTimeoutListener {
        fun onWorkTimeout(work: Work)
    }
}