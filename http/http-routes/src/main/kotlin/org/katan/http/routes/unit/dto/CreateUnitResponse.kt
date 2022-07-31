package org.katan.http.routes.unit.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUnitResponse(
    @SerialName("docker-image") val dockerImage: String,
    val unit: UnitResponse
)
