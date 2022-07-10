package org.katan.service.server

@kotlinx.serialization.Serializable
public data class UnitCreateOptions(
    val name: String,
    val externalId: String? = null,
    val displayName: String? = null,
    val description: String? = null
)