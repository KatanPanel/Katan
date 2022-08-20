package org.katan.http.di

import io.ktor.server.application.Application
import org.katan.http.websocket.WebSocketOp
import org.katan.http.websocket.WebSocketPacketEventHandler
import org.koin.core.component.KoinComponent

abstract class HttpModule(registry: HttpModuleRegistry) : KoinComponent {

    init {
        @Suppress("LeakingThis")
        registry.register(this)
    }

    abstract fun install(app: Application)

    open fun webSocketHandlers(): Map<WebSocketOp, WebSocketPacketEventHandler> {
        return emptyMap()
    }
}
