package me.devnatan.katan.api.io.websocket.message

import io.ktor.http.cio.websocket.WebSocketSession

open class KWSBaseMessage<out TData : Any>(
    override val id: String,
    override val session: WebSocketSession,
    override val content: TData
) : KWSMessage<TData>