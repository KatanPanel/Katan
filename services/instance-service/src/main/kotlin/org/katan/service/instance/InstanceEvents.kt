

@file:Suppress("ktlint:standard:filename")

package org.katan.service.instance

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
internal data class InstanceCreatedEvent(val instanceId: Snowflake, val blueprintId: Snowflake, val createdAt: Instant)
