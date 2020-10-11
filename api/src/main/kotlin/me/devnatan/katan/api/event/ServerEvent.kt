package me.devnatan.katan.api.event

import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerInspection
import me.devnatan.katan.api.server.ServerState
import java.time.Duration

open class ServerEvent(val server: Server) : Event

open class ServerCreateEvent(server: Server) : ServerEvent(server)

open class ServerComposedEvent(server: Server) : ServerEvent(server)

open class ServerStartEvent(server: Server, val duration: Duration) : ServerEvent(server)

open class ServerBeforeStartEvent(server: Server) : ServerEvent(server)

open class ServerStopEvent(server: Server, val duration: Duration) : ServerEvent(server)

open class ServerBeforeStopEvent(server: Server) : ServerEvent(server)

open class ServerInspectionEvent(
    server: Server,
    val result: ServerInspection
) : ServerEvent(server)

open class ServerStateChangeEvent(
    server: Server,
    val state: ServerState
) : ServerEvent(server) {

    val oldState: ServerState get() = server.state
    val newState: ServerState get() = state

}