package me.devnatan.katan.webserver.websocket

object WebSocketOpCode {

    const val OP_KEY = "op"
    const val DATA_KEY = "d"

    const val INVALID = -1

    /**
     * Direction: client
     */
    const val SERVER_UPDATED = 1000

    /**
     * Direction: server
     */
    const val SERVER_STATS = 1001

    /**
     * Direction: client
     */
    const val SERVER_LOGS_STARTED = 1002

    /**
     * Direction: server
     */
    const val SERVER_LOGS = 1003

    /**
     * Direction: client
     */
    const val SERVER_LOGS_FINISHED = 1004

    /**
     * Direction: server
     */
    const val SERVER_EXEC = 1005

    /**
     * Direction: server
     */
    const val SERVER_ATTACH = 1006

    /**
     * Direction: server, client
     */
    const val SERVER_START = 1007

    /**
     * Direction: server, client
     */
    const val SERVER_STOP = 1008

    /**
     * Direction: server
     */
    const val SERVER_STATS_START_STREAMING = 1009

    /**
     * Direction: server
     */
    const val SERVER_STATS_CANCEL_STREAMING = 1010

}