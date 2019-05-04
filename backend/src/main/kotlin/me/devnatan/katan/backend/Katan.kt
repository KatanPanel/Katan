package me.devnatan.katan.backend

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import me.devnatan.katan.backend.internal.SocketServer
import me.devnatan.katan.backend.io.readFile
import me.devnatan.katan.backend.message.Messenger
import me.devnatan.katan.backend.message.handler.InputServerHandler
import me.devnatan.katan.backend.message.handler.ServerHandlerPredicate
import me.devnatan.katan.backend.message.handler.StartServerHandler
import me.devnatan.katan.backend.message.handler.StopServerHandler
import me.devnatan.katan.backend.server.KServerManager
import me.devnatan.katan.backend.sql.Servers
import me.devnatan.katan.backend.util.asJsonMap
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

class Katan {

    companion object {

        val LOGGER: Logger = LoggerFactory.getLogger("Katan")!!

    }

    val coroutine: CoroutineScope = CoroutineScope(CoroutineName("Katan"))
    val actor = CoroutineScope(CoroutineName("Katan:Logging")).actor<String> {
        consumeEach {
            socketServer.broadcast(it)
        }
    }

    lateinit var json: ObjectMapper
    lateinit var locale: Map<*, *>
    lateinit var config: Map<*, *>

    lateinit var socketServer: SocketServer
    lateinit var serverManager: KServerManager
    lateinit var messenger: Messenger

    internal fun init() {
        locale = loadJsonResource("locale.json")
        config = loadJsonResource("katan.json")
        setupDatabase()
        load()
    }

    private fun load() {
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

    private fun setupDatabase() {
        val mysql = config["mysql"] as Map<*, *>
        Database.connect(mysql["url"] as String,
            mysql["driver"] as String,
            mysql["user"] as String,
            mysql["password"] as String)

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Servers)
        }
    }

    private fun loadJsonResource(resource: String): Map<*, *> {
        val f = File(this::class.java.classLoader.getResource(resource).file)
        if (!f.exists())
            throw FileNotFoundException("Couldn't find resource $resource")

        return String(readFile(f), StandardCharsets.UTF_8).asJsonMap()
            ?: throw IllegalArgumentException("Couldn't load resource $resource")
    }

}