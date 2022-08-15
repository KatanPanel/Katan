package org.katan.service.server.model

public data class UnitCreateOptions(
    val name: String,
    val externalId: String? = null,
    val dockerImage: String,
    val actorId: Long?
)
