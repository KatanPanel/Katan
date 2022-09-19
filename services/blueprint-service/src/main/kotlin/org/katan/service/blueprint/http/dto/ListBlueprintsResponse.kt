package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ListBlueprintsResponse(
    val blueprints: List<BlueprintResponse>
)
