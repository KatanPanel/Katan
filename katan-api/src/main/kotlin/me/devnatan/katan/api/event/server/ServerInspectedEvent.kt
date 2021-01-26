package me.devnatan.katan.api.event.server

import me.devnatan.katan.api.event.account.AccountEvent
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainerInspection

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