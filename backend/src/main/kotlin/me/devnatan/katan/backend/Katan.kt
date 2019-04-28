package me.devnatan.katan.backend

import com.google.gson.Gson
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.backend.server.KServerManager
import org.slf4j.Logger

object Katan {

    lateinit var logger: Logger
    lateinit var gson: Gson
    lateinit var webSocket: DefaultWebSocketServerSession

    lateinit var serverManager: KServerManager
    private val coroutine: CoroutineScope = CoroutineScope(CoroutineName("Katan"))

    internal fun init() {
        serverManager = KServerManager()
        serverManager.load(coroutine)
    }

}