package me.devnatan.katan.api.io

import me.devnatan.katan.api.Descriptor

interface FileSystemAccessor {

    suspend fun newSession(holder: Descriptor): FileSystemSession

}