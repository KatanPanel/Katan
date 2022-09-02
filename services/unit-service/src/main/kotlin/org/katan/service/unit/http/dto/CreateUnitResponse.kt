package org.katan.service.unit.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUnitResponse(
    val unit: UnitResponse
)
