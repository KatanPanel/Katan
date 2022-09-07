package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ImportBlueprintResponse(
    val id: String,
    val main: String,
    val assets: List<String>,
    val raw: RawBlueprintResponse,
)