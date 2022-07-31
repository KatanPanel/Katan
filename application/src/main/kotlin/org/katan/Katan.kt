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

    private val httpServer: HttpServer by lazy { HttpServer(config.server.port) }

    fun start() {
        httpServer.start()
    }

    fun close() {
        httpServer.stop()

        // close all possible services
        getAll<Closeable>().forEach { it.close() }
    }

    // https://github.com/InsertKoinIO/koin/issues/146#issuecomment-927189486
    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(KoinInternalApi::class)
    private inline fun <reified T : Any> getAll(): Collection<T> =
        getKoin().let { koin ->
            koin.instanceRegistry.instances.values.map { it.beanDefinition }
                .filter { it.kind == Kind.Singleton }
                .filter { it.primaryType.isSubclassOf(T::class) }
                .map { koin.get(clazz = it.primaryType, qualifier = null, parameters = null) }
        }

}