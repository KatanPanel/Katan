package me.devnatan.katan.io.file

import me.devnatan.katan.api.io.FileSystem
import me.devnatan.katan.api.io.FileSystemSession

interface PersistentFileSystem : FileSystem {

    suspend fun close(session: FileSystemSession)

}