package me.devnatan.katan.fs

import me.devnatan.katan.api.io.FileDisk
import me.devnatan.katan.api.server.Server

interface FileSystem {

    suspend fun listDisks(server: Server): List<FileDisk>

}