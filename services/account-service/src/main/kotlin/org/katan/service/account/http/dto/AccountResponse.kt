package org.katan.service.account.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
public data class AccountResponse internal constructor(
    val id: Long,
    val username: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("last_logged_in_at") val lastLoggedInAt: Instant?
) {

    public constructor(account: Account) : this(
        id = account.id,
        username = account.username,
        createdAt = account.createdAt,
        updatedAt = account.createdAt,
        lastLoggedInAt = account.lastLoggedInAt
    )
}
