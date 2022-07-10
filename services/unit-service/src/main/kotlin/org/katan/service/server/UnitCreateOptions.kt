package org.katan.service.server

@kotlinx.serialization.Serializable
public data class UnitCreateOptions(
    val externalId: String?,
    val name: String,
    val displayName: String?,
    val description: String?
)