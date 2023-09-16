package org.katan.model.io

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Bucket(
    val path: String,
    val name: String,
    val isLocal: Boolean,
    val createdAt: Instant?,
)
