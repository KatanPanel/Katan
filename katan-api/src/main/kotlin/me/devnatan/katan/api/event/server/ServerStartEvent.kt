package me.devnatan.katan.api.event.server

import me.devnatan.katan.api.event.account.AccountEvent
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server
import java.time.Duration

/**
 * Called when a server is started.
 * @property server the server.
 * @property account the account that started the server.
 * @property duration the duration of the server startup.
 */
open class ServerStartEvent(
    override val server: Server,
    override val account: Account? = null,
    val duration: Duration
) : ServerEvent, AccountEvent