package org.katan.service.server.http.dto

import jakarta.validation.constraints.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUnitRequest(
    @field:Size(min = 2, max = 128) val name: String,
    @field:Size(min = 2, max = 128) @SerialName("display-name")
    val displayName: String? = null,
    val description: String? = null,
    @SerialName("external-id") val externalId: String? = null,
    @SerialName("docker-image") val dockerImage: String
)
