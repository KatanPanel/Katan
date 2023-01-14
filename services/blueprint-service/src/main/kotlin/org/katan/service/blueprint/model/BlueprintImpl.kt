package org.katan.service.blueprint.model

import kotlinx.datetime.Instant
import org.katan.model.Snowflake
import org.katan.model.blueprint.Blueprint

internal data class BlueprintImpl(
    override val id: Snowflake,
    override val name: String,
    override val version: String,
    override val imageId: String,
    override val createdAt: Instant,
    override val updatedAt: Instant? = null
) : Blueprint
