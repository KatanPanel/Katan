package org.katan.service.server.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUnitResponse(
    val unit: UnitResponse
)
