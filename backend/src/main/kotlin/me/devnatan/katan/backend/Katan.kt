package me.devnatan.katan.backend

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.backend.internal.SocketServer
import me.devnatan.katan.backend.io.readFile
import me.devnatan.katan.backend.message.Messenger
import me.devnatan.katan.backend.message.handler.InputServerHandler
import me.devnatan.katan.backend.message.handler.ServerHandlerPredicate
import me.devnatan.katan.backend.message.handler.StartServerHandler
import me.devnatan.katan.backend.message.handler.StopServerHandler
import me.devnatan.katan.backend.server.KServerManager
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

class Katan {

    private val coroutine: CoroutineScope = CoroutineScope(CoroutineName("Katan"))
    val actor = CoroutineScope(CoroutineName("Katan:Logging")).actor<String> {
        consumeEach {
            socketServer.broadcast(it)
        }
    }

    lateinit var gson: Gson
    lateinit var locale: Map<*, *>

    lateinit var socketServer: SocketServer
    lateinit var serverManager: KServerManager
    lateinit var messenger: Messenger

    internal fun init() {
        runBlocking {
            loadLocale()
        }

        socketServer = SocketServer()
        messenger = Messenger().apply {
            addPredicate(
                ServerHandlerPredicate,
                StartServerHandler,
                StopServerHandler,
                InputServerHandler
            )
        }

        serverManager = KServerManager(this)
        serverManager.load(coroutine)
    }

    private suspend fun loadLocale() {
        val f = File(this::class.java.classLoader.getResource("locale.json").file)
        if (!f.exists())
            throw FileNotFoundException("Couldn't find locale file")

        readFile(coroutine, f) {
            this.locale = gson.fromJson(String(it, StandardCharsets.UTF_8), Map::class.java)
        }
    }

}