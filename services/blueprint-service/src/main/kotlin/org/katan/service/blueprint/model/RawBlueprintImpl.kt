package org.katan.service.blueprint.model

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.RawBlueprint

@Serializable
internal data class RawBlueprintImpl(
    override val name: String,
    override val version: String,
    override val author: String,
    override val image: String
) : RawBlueprint