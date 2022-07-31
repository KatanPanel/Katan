package org.katan.service.server

import kotlinx.serialization.Serializable

@Serializable
public data class UnitCreateOptions(
    val name: String,
    val externalId: String? = null,
    val displayName: String? = null,
    val description: String? = null,
    val dockerImage: String
)
