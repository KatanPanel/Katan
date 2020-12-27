package me.devnatan.katan.api.event

import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainerInspection
import me.devnatan.katan.api.server.ServerState
import java.time.Duration

/**
 * Represents an event that contains an [Server] in its context.
 */
interface ServerEvent : Event {

    /**
     * Returns the server involved with the event.
     */
    val server: Server?

}

/**
 * Called when a new server is created, when all compositions on a server are applied to it.
 * @property server the server.
 * @property account the account that created the server.
 */
open class ServerCreateEvent(
    override val server: Server,
    override val account: Account? = null,
) : ServerEvent, AccountEvent

/**
 * Called when a server's start process starts.
 * @property server the server.
 * @property account the account that requested the server start.
 */
open class ServerStartingEvent(
    override val server: Server,
    override val account: Account? = null,
) : ServerEvent, AccountEvent

/**
 * Called when a server is started.
 * @property server the server.
 * @property account the account that started the server.
 * @property duration the duration of the server startup.
 */
open class ServerStartedEvent(
    override val server: Server,
    override val account: Account? = null,
    val duration: Duration
) : ServerEvent, AccountEvent

/**
 * Called when a server's stop process starts.
 * @property server the server.
 * @property account the account that requested the server stop.
 */
open class ServerStoppingEvent(
    override val server: Server,
    override val account: Account? = null,
) : ServerEvent, AccountEvent

/**
 * Called when a server is stopped.
 * @property server the server.
 * @property account the account that stopped the server.
 * @property duration the duration of the server outage.
 */
open class ServerStoppedEvent(
    override val server: Server,
    override val account: Account? = null,
    val duration: Duration
) : ServerEvent, AccountEvent

/**
 * Called when a server's inspection process is completed.
 * @property server the server
 * @property account the account that requested the server inspection.
 * @property result the result of the server inspection.
 */
open class ServerInspectedEvent(
    override val server: Server?,
    override val account: Account? = null,
    val result: ServerContainerInspection
) : ServerEvent, AccountEvent

/**
 * Called when a server's state changes, related to events [ServerStartedEvent] and [ServerStoppedEvent].
 * @property server the server
 * @property state the new state of the server.
 */
open class ServerStateChangeEvent(override val server: Server, val state: ServerState) : ServerEvent {

    /**
     * The state of the server before the change.
     */
    val oldState: ServerState get() = server.state

    /**
     * The state of the server after the change.
     */
    val newState: ServerState get() = state

}