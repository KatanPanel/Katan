package me.devnatan.katan.core.fs

import me.devnatan.katan.api.io.File
import me.devnatan.katan.fs.disk.AbstractFileDisk
import java.time.Instant

class DockerFileDisk(
    override val name: String,
    override val path: String,
    override val size: Long,
    override val kind: String,
    override val createdAt: Instant?
) : AbstractFileDisk() {

    override val id: String
        get() = name

    override val isHidden: Boolean
        get() = false

    override val isProtected: Boolean
        get() = false

    override suspend fun listFiles(): List<File> {
        return emptyList()
    }

}