package org.katan.http

import io.ktor.server.application.Application
import org.katan.http.websocket.WebSocketOp
import org.katan.http.websocket.WebSocketPacketEventHandler
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.core.qualifier.named

abstract class HttpModule : KoinComponent {

    open val priority: Int get() = 0

    abstract fun install(app: Application)

    open fun webSocketHandlers(): Map<WebSocketOp, WebSocketPacketEventHandler> {
        return emptyMap()
    }
}

inline fun <reified T : HttpModule> Module.importHttpModule(crossinline module: () -> T) {
    single(
        qualifier = named<T>(),
        createdAtStart = true
    ) {
        val module = module()
        get<HttpModuleRegistry>() += module
        module
    }
}
