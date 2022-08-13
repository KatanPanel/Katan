package org.katan.service.server.model

import kotlinx.serialization.Serializable

@Serializable
public data class UnitUpdateOptions(
    val name: String? = null
)
