package com.capstone.app.utrace_cts.bluetooth

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.capstone.app.utrace_cts.CentralDevice
import org.apache.commons.text.StringEscapeUtils


//this is for the payload of write operations (OnCharacteristicRead for CENTRAL)

class BluetoothWritePayload (
    val v: Int,
    val id: String,
    central: CentralDevice,
    val rs: Int
                             ){
    fun getPayload(): ByteArray{
        return gson.toJson(this).toByteArray(Charsets.UTF_8)
    }

    val mc: String = central.modelC

    companion object {
        //val gson: Gson
        val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

        fun fromPayload(databytes: ByteArray): BluetoothWritePayload {
            val dataString = String(databytes, Charsets.UTF_8)
            return gson.fromJson(dataString, BluetoothWritePayload::class.java)
        }

        fun removeQuotesAndUnescape(uncleanJson: String): String{
            val noQuotes = uncleanJson.replace("^\"|\"$", "")
            return StringEscapeUtils.unescapeJava(noQuotes)
        }
    }

}