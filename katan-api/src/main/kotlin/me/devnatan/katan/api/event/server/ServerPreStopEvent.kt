package me.devnatan.katan.api.event.server

import me.devnatan.katan.api.event.account.AccountEvent
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server

/**
 * Called when a server's stop process starts.
 * @property server the server.
 * @property account the account that requested the server stop.
 */
open class ServerPreStopEvent(
    override val server: Server,
    override val account: Account? = null,
) : ServerEvent, AccountEvent