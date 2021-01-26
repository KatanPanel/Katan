package me.devnatan.katan.api.io

import me.devnatan.katan.api.Platform
import java.time.Instant

/**
 * Represents a single file or directory on the file system on that [Platform].
 */
interface File {

    /**
     * Returns the name (with extension) of this [File].
     */
    val name: String

    /**
     * Returns the path to that file.
     */
    val path: String

    /**
     * Returns when the file was created or `null` if the information is not available.
     */
    val createdAt: Instant?

    val lastModifiedAt: Instant?

    /**
     * Returns the length of the file.
     */
    val size: Long

    /**
     * Returns if this file is hidden according to the conventions of the underlying [Platform].
     */
    val isHidden: Boolean

    /**
     * Returns `true` if this is a directory or `false` otherwise.
     */
    val isDirectory: Boolean

    val origin: FileOrigin

    suspend fun listFiles(): List<File>

}

inline class FileOrigin(val value: String) {

    companion object {

        val LOCAL = FileOrigin("local")
        val REMOTE = FileOrigin("remote")
        val EXTERNAL = FileOrigin("external")

    }

    fun isLocal(): Boolean {
        return this == LOCAL
    }

    fun isRemote(): Boolean {
        return this == REMOTE
    }

    fun isExternal(): Boolean {
        return this == EXTERNAL
    }

}