package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.Blueprint

@Serializable
internal data class BlueprintResponse(
    val id: String,
    val name: String,
    val image: String
) {

    constructor(blueprint: Blueprint) : this(
        id = blueprint.id.toString(),
        name = blueprint.name,
        image = blueprint.image
    )

}