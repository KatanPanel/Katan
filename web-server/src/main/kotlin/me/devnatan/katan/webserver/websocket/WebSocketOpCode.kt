package me.devnatan.katan.webserver.websocket

object WebSocketOpCode {

    const val OP_KEY = "op"
    const val DATA_KEY = "d"

    const val SERVER_LOGS = 1
    const val SERVER_ATTACH = 2
    const val SERVER_STATS = 3

}