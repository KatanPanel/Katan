package me.devnatan.katan.api.io

import me.devnatan.katan.api.Descriptor
import java.time.Instant
import java.util.*

interface FileSystemSession : FileSystem {

    val id: UUID

    val holder: Descriptor

    val startedAt: Instant

    var lastAccess: Instant?

    fun isClosed(): Boolean

}