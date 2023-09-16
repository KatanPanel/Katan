package org.katan.model.blueprint

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
data class Blueprint(
    val id: Snowflake,
    val createdAt: Instant,
    val updatedAt: Instant,
    val spec: BlueprintSpec,
)
