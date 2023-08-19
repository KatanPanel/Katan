package org.katan.service.unit

import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
public data class UnitCreatedEvent(public val unitId: Snowflake, public val name: String, public val nodeId: Int)
