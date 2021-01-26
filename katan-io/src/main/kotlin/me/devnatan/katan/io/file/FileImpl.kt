package me.devnatan.katan.io.file

import me.devnatan.katan.api.io.File
import me.devnatan.katan.api.io.FileOrigin
import java.time.Instant

data class FileImpl(
    override val name: String,
    override val path: String,
    override val size: Long,
    override val origin: FileOrigin,
    override val createdAt: Instant?,
    override val lastModifiedAt: Instant?,
    override val isDirectory: Boolean,
    override val isHidden: Boolean
) : File {

    override suspend fun listFiles(): List<File> {
        if (!isDirectory)
            throw IllegalArgumentException("Not a directory")

        TODO()
    }

}