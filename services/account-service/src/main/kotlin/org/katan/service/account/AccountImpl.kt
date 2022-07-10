package org.katan.service.account

import kotlinx.datetime.Instant
import org.katan.model.account.Account

@kotlinx.serialization.Serializable
internal data class AccountImpl(
    override val id: Long,
    override val username: String,
    override val registeredAt: Instant
) : Account {

    override val lastLoggedInAt: Instant? = null
    override val deactivated: Boolean = false

}