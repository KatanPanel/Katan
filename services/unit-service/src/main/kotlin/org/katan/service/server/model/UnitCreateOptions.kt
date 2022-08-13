package org.katan.service.server.model

import kotlinx.serialization.Serializable

@Serializable
public data class UnitCreateOptions(
    val name: String,
    val externalId: String? = null,
    val dockerImage: String
)
