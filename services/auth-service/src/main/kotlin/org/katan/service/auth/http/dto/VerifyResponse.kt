package org.katan.service.auth.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
internal data class VerifyResponse internal constructor(
    val id: String,
    val username: String,
    val email: String,
    @SerialName("created-at") val createdAt: Instant,
    @SerialName("updated-at") val updatedAt: Instant,
    @SerialName("last-logged-in-at") val lastLoggedInAt: Instant?
) {

    constructor(account: Account) : this(
        id = account.id.value.toString(),
        username = account.username,
        email = account.email,
        createdAt = account.createdAt,
        updatedAt = account.createdAt,
        lastLoggedInAt = account.lastLoggedInAt
    )
}
