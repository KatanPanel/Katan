package me.devnatan.katan.api.event.server

import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerState

/**
 * Called when a server's state changes, related to events [ServerStartEvent] and [ServerStopEvent].
 * @property server the server
 * @property oldState the old state of the server.
 * @property newState the new state of the server.
 */
open class ServerStateChangeEvent(
    override val server: Server,
    val oldState: ServerState,
    val newState: ServerState
) : ServerEvent