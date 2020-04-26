package me.devnatan.katan.core.util

import me.devnatan.katan.api.server.KServerQuery
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import kotlin.system.measureTimeMillis

class ServerQuery {

    companion object {

        @JvmStatic
        fun query(address: InetSocketAddress): KServerQuery? {
            val socket = Socket()
            return try {
                val latency = measureTimeMillis {
                    socket.connect(address, 3000)
                }

                val writter = DataOutputStream(socket.getOutputStream())
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                socket.use {
                    writter.write(byteArrayOf(0xFE.toByte(), 0x01.toByte()))
                    val line = reader.readLine() ?: return null
                    val response = line.split("\u0000\u0000\u0000").map {
                        it.replace("\u0000", "")
                    }

                    KServerQuery(address,
                        response[2],
                        response[3],
                        response[4].toInt(),
                        response[5].toInt(),
                        latency,
                        true
                    )
                }
            } catch (e: SocketException) {
                null
            }
        }

    }

}