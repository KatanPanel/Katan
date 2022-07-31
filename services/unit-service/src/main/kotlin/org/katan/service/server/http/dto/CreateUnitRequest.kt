package org.katan.service.server.http.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUnitRequest(
    val name: String,
    @SerialName("display-name") val displayName: String? = null,
    val description: String? = null,
    @SerialName("external-id") val externalId: String? = null,
    @SerialName("docker-image") val dockerImage: String
)
