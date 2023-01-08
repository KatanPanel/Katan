package org.katan.service.instance.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.io.Bucket

@Serializable
internal data class FSBucketResponse(
    val path: String,
    val name: String,
    @SerialName("is-local") val isLocal: Boolean,
    @SerialName("created-at") val createdAt: Instant?
) {

    internal constructor(bucket: Bucket) : this(
        path = bucket.path,
        name = bucket.name,
        isLocal = bucket.isLocal,
        createdAt = bucket.createdAt
    )
}
