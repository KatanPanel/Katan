package me.devnatan.katan.core.database

import java.io.Closeable

interface DatabaseConnector<S : DatabaseSettings> : Closeable {

    val name: String

    val driver: String

    val url: String

    suspend fun connect(settings: S)

    fun createConnectionUrl(settings: S): String

}