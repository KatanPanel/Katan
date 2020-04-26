package me.devnatan.katan.api.io.websocket

import io.ktor.http.cio.websocket.WebSocketSession
import kotlinx.coroutines.isActive
import java.io.Closeable

/**
 * @property session current active websocket session.
 * @property stream bridge between the session and the server.
 */
open class KWSSession(
    val session: WebSocketSession,
    val stream: Closeable
) {

    /**
     * Closes the [stream] between the server and the current [session].
     * @throws IllegalStateException if the session is no longer active.
     */
    @Throws(IllegalStateException::class)
    fun close() {
        check(session.isActive) { "Stream already closed" }
        stream.close()
    }

}