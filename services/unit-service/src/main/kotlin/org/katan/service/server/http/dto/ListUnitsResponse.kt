package org.katan.service.server.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ListUnitsResponse(
    val units: List<UnitResponse>
)
