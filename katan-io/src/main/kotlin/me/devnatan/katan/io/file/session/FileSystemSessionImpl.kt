package me.devnatan.katan.io.file.session

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.io.File
import me.devnatan.katan.api.io.FileDisk
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.io.file.PersistentFileSystem
import java.time.Instant

class FileSystemSessionImpl(
    holder: Descriptor,
    @JvmField private val fs: PersistentFileSystem,
) : AbstractFileSystemSession(holder) {

    override val startedAt: Instant = Instant.now()

    override suspend fun isProtected(file: File): Boolean {
        updateLastAccess()
        return fs.isProtected(file)
    }

    override suspend fun getDisk(server: Server, id: String): FileDisk? {
        updateLastAccess()
        return fs.getDisk(server, id)
    }

    override suspend fun listDisks(server: Server): List<FileDisk> {
        updateLastAccess()
        return fs.listDisks(server)
    }

    override suspend fun close() {
        fs.close(this)
    }

    private fun updateLastAccess() {
        lastAccess = Instant.now()
    }

    override fun toString(): String {
        return "$id ($holder)"
    }

}