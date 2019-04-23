package me.devnatan.katan.frontend

import me.devnatan.katan.frontend.socket.Socket

object Main {

    private lateinit var socket: Socket

    internal fun init() {
        socket = Socket()
        socket.connect()
    }

}

fun main() {
    Main.init()
}