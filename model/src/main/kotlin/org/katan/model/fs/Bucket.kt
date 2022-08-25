package org.katan.model.fs

import kotlinx.datetime.Instant

interface Bucket {

    val path: String

    val name: String

    val isLocal: Boolean

    val createdAt: Instant?

}