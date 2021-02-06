package me.devnatan.katan.webserver.websocket

object WebSocketOpCode {

    const val OP_KEY = "op"
    const val DATA_KEY = "d"

    const val INVALID = -1
    const val SERVER_INFO = 1000
    const val SERVER_STATS = 1001
    const val SERVER_LOGS_STARTED = 1002
    const val SERVER_LOGS = 1003
    const val SERVER_LOGS_FINISHED = 1004
    const val SERVER_EXEC = 1005
    const val SERVER_ATTACH = 1006
    const val SERVER_START = 1007
    const val SERVER_STOP = 1008

}