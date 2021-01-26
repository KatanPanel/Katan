package me.devnatan.katan.io.file.session

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.io.FileSystemSession
import java.time.Instant
import java.util.*

abstract class AbstractFileSystemSession(
    override val holder: Descriptor
) : FileSystemSession {

    override val id: UUID by lazy { UUID.randomUUID() }
    private var closed: Boolean = true

    override var lastAccess: Instant? = null

    override fun isClosed(): Boolean {
        return closed
    }

}