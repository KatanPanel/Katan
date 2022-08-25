package org.katan.http.websocket

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

abstract class WebSocketPacketEventHandler : CoroutineScope {

    override lateinit var coroutineContext: CoroutineContext internal set

    abstract suspend fun WebSocketPacketContext.handle()
}
