package org.katan.service.instance.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateInstanceOptions(
    val image: String,
    val host: String? = null,
    val port: Int? = null,
)
