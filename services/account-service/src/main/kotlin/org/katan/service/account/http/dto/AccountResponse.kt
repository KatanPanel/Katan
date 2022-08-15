package org.katan.service.account.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
public data class AccountResponse internal constructor(
    val id: String,
    val username: String,
    @SerialName("created-at") val createdAt: Instant,
    @SerialName("updated-at") val updatedAt: Instant,
    @SerialName("last-logged-in-at") val lastLoggedInAt: Instant?
) {

    public constructor(account: Account) : this(
        id = account.id.toString(),
        username = account.username,
        createdAt = account.createdAt,
        updatedAt = account.createdAt,
        lastLoggedInAt = account.lastLoggedInAt
    )
}
