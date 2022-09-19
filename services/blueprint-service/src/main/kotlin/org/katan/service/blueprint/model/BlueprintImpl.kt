package org.katan.service.blueprint.model

import kotlinx.datetime.Instant
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint

internal data class BlueprintImpl(
    override val id: Long,
    override val name: String,
    override val version: String,
    override val imageId: String,
    override val createdAt: Instant,
    override val updatedAt: Instant?,
    override val raw: RawBlueprint?
) : Blueprint
