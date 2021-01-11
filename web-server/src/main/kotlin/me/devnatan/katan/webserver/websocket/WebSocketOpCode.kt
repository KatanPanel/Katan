package me.devnatan.katan.webserver.websocket

object WebSocketOpCode {

    const val OP_KEY = "op"
    const val DATA_KEY = "d"

    const val SERVER_INFO = 1
    const val SERVER_STATS = 2
    const val SERVER_LOGS_STARTED = 3
    const val SERVER_LOGS = 4
    const val SERVER_LOGS_FINISHED = 5
    const val SERVER_EXEC = 6
    const val SERVER_ATTACH = 7

}