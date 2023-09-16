package org.katan.model.account

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
data class Account(
    val id: Snowflake,
    val username: String,
    val email: String,
    val displayName: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastLoggedInAt: Instant?,
    val avatar: Snowflake?,
)
