package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.Server
import java.io.File
import java.nio.file.Files

class ServerLocalDataManager {

    private val dataFolder = File(".data")

    init {
        dataFolder.mkdir()
    }

    private fun checkPath(parent: File, name: String, dir: Boolean = true): File {
        val file = File(parent, name)
        if (dir && !file.exists()) Files.createDirectory(file.toPath())
        else file.parentFile.mkdirs()
        return file
    }

    private fun getDataFolder(server: Server): File {
        return checkPath(dataFolder, server.container.id)
    }

    private fun getCompositionsDataFolder(server: Server): File {
        return checkPath(getDataFolder(server), "compositions")
    }

    private fun getCompositionDataFolder(server: Server, key: String): File {
        return checkPath(getCompositionsDataFolder(server), key)
    }

    fun getCompositionOptions(server: Server, key: String): File {
        return checkPath(getCompositionDataFolder(server, key), "options.json", false)
    }

}