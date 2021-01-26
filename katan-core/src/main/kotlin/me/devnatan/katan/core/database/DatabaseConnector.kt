package me.devnatan.katan.core.database

import java.io.Closeable

interface DatabaseConnector : Closeable {

    val name: String

    val driver: String

    val url: String

    suspend fun connect(settings: DatabaseSettings)

    fun createConnectionUrl(settings: DatabaseSettings): String

}