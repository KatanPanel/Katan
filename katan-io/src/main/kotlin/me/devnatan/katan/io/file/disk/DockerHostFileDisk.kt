package me.devnatan.katan.io.file.disk

import me.devnatan.katan.api.io.File
import me.devnatan.katan.api.io.FileOrigin
import me.devnatan.katan.io.file.DockerHostFileSystem
import me.devnatan.katan.io.file.FileImpl
import java.time.Instant

data class DockerHostFileDisk(
    override val name: String,
    override val path: String,
    override val size: Long,
    override val origin: FileOrigin,
    override val createdAt: Instant?,
    override val lastModifiedAt: Instant?,
    @JvmField private val fs: DockerHostFileSystem
) : AbstractFileDisk() {

    override val id: String
        get() = name

    override val isHidden: Boolean get() = false

    override suspend fun listFiles(): List<File> {
        fs.checkAvailability()
        val dir = java.io.File(path)
        if (!dir.exists())
            return emptyList()

        return dir.listFiles()?.map { fromJavaFile(it) } ?: emptyList()
    }

    private fun fromJavaFile(file: java.io.File): File {
        return FileImpl(
            file.name,
            file.path,
            file.length(),
            DockerHostFileSystem.FILE_SYSTEM_ORIGIN,
            null,
            Instant.ofEpochMilli(file.lastModified()),
            file.isDirectory,
            file.isHidden
        )
    }

    override fun toString(): String {
        return "$name @ $path"
    }

}