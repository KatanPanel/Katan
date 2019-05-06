package me.devnatan.katan.backend

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import me.devnatan.katan.backend.message.Messenger
import me.devnatan.katan.backend.message.handler.InputServerHandler
import me.devnatan.katan.backend.message.handler.ServerHandlerPredicate
import me.devnatan.katan.backend.message.handler.StartServerHandler
import me.devnatan.katan.backend.message.handler.StopServerHandler
import me.devnatan.katan.backend.server.ServerController
import me.devnatan.katan.backend.server.query.ServerQueryTask
import me.devnatan.katan.backend.socket.SocketController
import me.devnatan.katan.backend.sql.server.Servers
import me.devnatan.katan.backend.util.asJsonMap
import me.devnatan.katan.backend.util.readStream
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
            socketController.broadcast(it)
        }
    }

    // KATAN
    val ftp: KatanFTP = KatanFTP()
    lateinit var json: ObjectMapper
    lateinit var locale: Map<*, *>
    lateinit var config: Map<*, *>
    lateinit var messenger: Messenger

    // CONTROLLERS
    lateinit var socketController: SocketController
    lateinit var serverController: ServerController

    // OTHER
    private val query: ServerQueryTask = ServerQueryTask()

    internal fun init() {
        config = loadJsonResource("katan.json")
        try {
            setupDatabase()
            locale = loadJsonResource("locale.json")
            load()
        } catch (e: Throwable) {}
    }

    private fun load() {
        socketController = SocketController()
        messenger = Messenger().apply {
            addPredicate(
                ServerHandlerPredicate,
                StartServerHandler,
                StopServerHandler,
                InputServerHandler
            )
        }

        serverController = ServerController(this)
        serverController.load()
        query.run()
        ftp.init()
    }

    private fun setupDatabase() {
        val mysql = config["mysql"] as Map<*, *>
        Database.connect(
            mysql["url"] as String,
            mysql["driver"] as String,
            mysql["user"] as String,
            mysql["password"] as String
        )

        transaction {
            try {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(Servers)
            } catch (e: Throwable) {
                LOGGER.error("[SQL] Failed to create default schema.")
                LOGGER.error("[SQL] Please verify your credentials.")
                System.exit(1)
            }
        }
    }

    private fun loadJsonResource(resource: String): Map<*, *> {
        val f = File(this::class.java.classLoader.getResource(resource).file)
        if (!f.exists())
            throw FileNotFoundException("Couldn't find resource $resource")

        return String(f.readStream(), StandardCharsets.UTF_8).asJsonMap()
            ?: throw IllegalArgumentException("Couldn't load resource $resource")
    }

}