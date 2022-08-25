package org.katan.http.websocket

typealias WebSocketOp = Int

object WebSocketOpCodes {

    const val FETCH_INSTANCE_LOGS: WebSocketOp = 0

    const val EXECUTE_INSTANCE_COMMAND: WebSocketOp = 1
}
