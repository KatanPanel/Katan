package me.devnatan.katan.webserver.websocket

object WebSocketOpCode {

    const val OP_KEY = "op"
    const val DATA_KEY = "d"

    const val SERVER_CREATE = 1
    const val SERVER_START = 2
    const val SERVER_STOP = 3
    const val SERVER_LOG = 4
    const val SERVER_ATTACH = 5
    const val SERVER_STATS = 6

}