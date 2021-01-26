package me.devnatan.katan.api.event.server

import me.devnatan.katan.api.event.Event
import me.devnatan.katan.api.server.Server

/**
 * Represents an event that contains an [Server] in its context.
 */
interface ServerEvent : Event {

    /**
     * Returns the server involved with the event.
     */
    val server: Server?

}