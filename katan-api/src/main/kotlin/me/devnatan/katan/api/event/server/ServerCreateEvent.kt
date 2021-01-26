package me.devnatan.katan.api.event.server

import me.devnatan.katan.api.event.account.AccountEvent
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server

/**
 * Called when a new server is created, when all compositions on a server are applied to it.
 * @property server the server.
 * @property account the account that created the server.
 */
open class ServerCreateEvent(
    override val server: Server,
    override val account: Account? = null,
) : ServerEvent, AccountEvent