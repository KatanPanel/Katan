package org.katan.service.blueprint.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.BlueprintSpec

@Serializable
internal data class BlueprintImpl(
    override val id: Snowflake,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val spec: BlueprintSpec
) : Blueprint
