package org.katan.service.account.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
internal data class RegisterResponse internal constructor(
    val id: String,
    val username: String,
    val displayName: String?,
    val email: String,
    @SerialName("created-at") val createdAt: Instant
) {

    constructor(account: Account) : this(
        id = account.id.value.toString(),
        username = account.username,
        displayName = account.displayName,
        email = account.email,
        createdAt = account.createdAt
    )
}