package me.devnatan.katan.api.io.websocket

import me.devnatan.katan.api.io.websocket.message.KWSLogMessage
import me.devnatan.katan.api.io.websocket.message.KWSServerMessage

interface KWSHandler {

    /**
     * Called when a new server is created by the user.
     */
    suspend fun onServerCreate(message: KWSServerMessage)

    /**
     * Called when a server starts the boot process.
     */
    suspend fun onServerStart(message: KWSServerMessage)

    /**
     * Called when a server starts the termination process.
     */
    suspend fun onServerStop(message: KWSServerMessage)

    /**
     * Called when a new record is to be sent to the server.
     */
    suspend fun onServerLog(message: KWSLogMessage)

    /**
     * Called when the user is linked to the server
     * and wants to receive records from that server.
     */
    suspend fun onServerAttach(message: KWSServerMessage)

}