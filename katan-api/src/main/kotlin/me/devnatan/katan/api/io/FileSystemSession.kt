package me.devnatan.katan.api.io

import me.devnatan.katan.api.Descriptor
import java.time.Instant
import java.util.*

interface FileSystemSession : FileSystem {

    val uid: UUID

    val holder: Descriptor

    val startedAt: Instant

    var lastAccess: Instant?

    suspend fun open()

    fun isClosed(): Boolean

}