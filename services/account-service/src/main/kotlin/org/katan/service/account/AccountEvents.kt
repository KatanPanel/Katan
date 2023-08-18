package org.katan.service.account

import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
data class AccountCreatedEvent(val id: Snowflake)

@Serializable
data class AccountDeletedEvent(val id: Snowflake)
