package me.devnatan.katan.api.io

import me.devnatan.katan.api.security.Credential
import me.devnatan.katan.api.server.Server

interface FileSystem {

    /**
     * Returns `true` if the [File] is protected by any type of [Credential] or `false` otherwise.
     */
    suspend fun isProtected(file: File): Boolean

    suspend fun listDisks(server: Server): List<FileDisk>

    suspend fun close()

}