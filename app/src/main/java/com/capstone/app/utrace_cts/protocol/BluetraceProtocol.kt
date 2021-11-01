package com.capstone.app.utrace_cts.protocol

import android.os.Build
import android.util.Log
import com.capstone.app.utrace_cts.bluetooth.BluetoothPayload
import com.capstone.app.utrace_cts.bluetooth.BluetoothWritePayload
import com.capstone.app.utrace_cts.CentralDevice
import com.capstone.app.utrace_cts.ConnectionRecord
import com.capstone.app.utrace_cts.PeripheralDevice

class BluetraceProtocol(
    val versionInt: Int,
    val central: CentralInterface,
    val peripheral: PeripheralInterface
)

    interface PeripheralInterface {
        fun prepareReadRequestData(protocolVersion: Int): ByteArray
        //needs to be in try-catch in case of parse failure
        fun processWriteRequestDataReceived(
                dataWritten: ByteArray,
                centralAddress: String
        ): ConnectionRecord?
    }

    interface CentralInterface {
        fun prepareWriteRequestData(protocolVersion: Int, rssi: Int, txPower: Int?): ByteArray

        //needs to be in try-catch in case of parse failure
        fun processReadRequestDataReceived(
                dataRead: ByteArray,
                peripheralAddress: String,
                rssi: Int,
                txPower: Int?
        ): ConnectionRecord?
    }

class V2Peripheral: PeripheralInterface {
    override fun prepareReadRequestData(protocolVersion: Int): ByteArray {
        return BluetoothPayload(v = protocolVersion,"1", PeripheralDevice(Build.MODEL, "SELF"), ).getPayload()
    }

    override fun processWriteRequestDataReceived(dataReceived: ByteArray, centralAddress: String): ConnectionRecord? {
        try{
            val dataWritten = BluetoothWritePayload.fromPayload(dataReceived)

            return ConnectionRecord(version = dataWritten.v,
                                    msg = dataWritten.id,
                                    peripheral = PeripheralDevice(Build.MODEL, "SELF"),
                                    central = CentralDevice(Build.MODEL, "SELF"),
                                    rssi = dataWritten.rs,
                                    txPower = null
            )
        }catch(e: Throwable){
            Log.e("BluetraceProtocl", "Failed to deserialize write payload ${e.message}")
        }
        return null
    }
}
class V2Central: CentralInterface {
    override fun prepareWriteRequestData(protocolVersion: Int, rssi: Int, txPower: Int?): ByteArray {
        return BluetoothWritePayload(v = protocolVersion, id = "1", CentralDevice(Build.MODEL, "SELF"),rs = rssi).getPayload()
    }
    override fun processReadRequestDataReceived(dataRead: ByteArray, peripheralAddress: String, rssi: Int, txPower: Int?): ConnectionRecord? {
        try{
            val readData = BluetoothPayload.fromPayload(dataRead)

            var peripheral = PeripheralDevice(readData.mp, peripheralAddress)

            var connectionRecord = ConnectionRecord(
                    version = readData.v,
                    msg = readData.id,
                    peripheral = peripheral,
                    central =  CentralDevice(Build.MODEL, "SELF"),
                    rssi = rssi,
                    txPower = txPower
            )
            return connectionRecord
        } catch(e: Throwable){
            Log.e("V2Central", "Failed to deserialized read payload ${e.message}")
        }
        return null
    }

}