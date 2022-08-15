package org.katan.service.account

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
internal data class AccountImpl(
    override val id: Long,
    override val username: String,
    override val email: String,
    override val displayName: String?,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val lastLoggedInAt: Instant?,
    override val avatar: Long?
) : Account
