package org.katan.model.io

import kotlinx.datetime.Instant

interface Bucket {

    val path: String

    val name: String

    val isLocal: Boolean

    val createdAt: Instant?
}
