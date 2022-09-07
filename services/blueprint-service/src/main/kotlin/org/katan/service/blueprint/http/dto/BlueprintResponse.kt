package org.katan.service.blueprint.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.blueprint.Blueprint

@Serializable
internal data class BlueprintResponse(
    val id: String,
    @SerialName("created-at") val createdAt: Instant,
    @SerialName("updated-at") val updatedAt: Instant?,
    val raw: RawBlueprintResponse?
) {

    constructor(blueprint: Blueprint) : this(
        id = blueprint.id.toString(),
        createdAt = blueprint.createdAt,
        updatedAt = blueprint.updatedAt,
        raw = blueprint.raw?.let(::RawBlueprintResponse)
    )

}