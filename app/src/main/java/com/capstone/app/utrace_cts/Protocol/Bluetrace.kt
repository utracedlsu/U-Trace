package com.capstone.app.utrace_cts.Protocol

import java.util.*

object Bluetrace {

    val implementations = mapOf<Int, BluetraceProtocol>(
              2 to BluetraceProtocol(2, V2Central(), V2Peripheral())
    )

    val characteristicToProtocolVersionMap = mapOf<UUID, Int>(
            UUID.fromString("011019d0-8cb6-4804-8b83-1c3348a8940c") to 2
    )

    fun supportsCharUUID(charUUID: UUID?): Boolean{
        if(charUUID == null){
            return false
        }
        characteristicToProtocolVersionMap[charUUID]?.let{
            version -> return implementations[version] != null
        }
        return false
    }
    fun getImplementation(charUUID: UUID): BluetraceProtocol {
        val protocolVersion = characteristicToProtocolVersionMap[charUUID]?:1
        return getImplementation(protocolVersion)
    }

    fun getImplementation(protocolVersion: Int): BluetraceProtocol {
        val impl = implementations[protocolVersion]

        return impl ?: BluetraceProtocol(2, V2Central(), V2Peripheral())
    }


}