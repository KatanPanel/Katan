package org.katan

import org.katan.config.KatanConfig
import org.katan.http.server.HttpServer
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.definition.Kind
import java.io.Closeable
import kotlin.reflect.full.isSubclassOf

class Katan : KoinComponent {

    private val config: KatanConfig by inject()
    private val httpServer: HttpServer by lazy { HttpServer(config.port) }

    fun start() {
        httpServer.start()
    }

    fun close() {
        httpServer.stop()
    }

}
