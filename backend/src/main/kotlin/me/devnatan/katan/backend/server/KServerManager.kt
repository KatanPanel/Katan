package me.devnatan.katan.backend.server

import io.ktor.http.cio.websocket.Frame
import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.backend.Katan
import me.devnatan.katan.backend.io.createProcess
import me.devnatan.katan.backend.util.asJsonString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class KServerManager {

    private val logger: Logger = LoggerFactory.getLogger(this::class.simpleName)
    private val servers = ConcurrentSet<KServer>()

    fun getServers(): Set<KServer> {
        return Collections.unmodifiableSet(servers)
    }

    fun getServer(id: String): KServer? = servers.find { it.id.equals(id, true) }

    fun load(coroutine: CoroutineScope) {
        val folder = File(this::class.java.classLoader.getResource("servers").file)
        if (!folder.exists())
            folder.mkdir()

        val folders = folder.listFiles() ?: return
        for (serverPath in folders) {
            if (!serverPath.isDirectory) continue

            val serverId = serverPath.name
            if (serverId.startsWith("-")) {
                logger.info("Ignoring server \"$serverId\".")
                continue
            }

            if (!serverPath.canRead()) {
                logger.error("Access denied to read files in \"$serverId\".")
                continue
            }

            loadServer(coroutine, serverId, serverPath)
        }
        logger.info("Loaded ${servers.size} servers.")
    }

    fun loadServer(coroutine: CoroutineScope, id: String, path: File): KServer {
        val jar = path.listFiles().firstOrNull {
            it.name.endsWith(".jar")
        } ?: throw FileNotFoundException("Couldn't find PaperSpigot for server \"$id\".")

        val server = KServer(
            id,
            KServerPath(path.absolutePath, jar.name),
            createProcess(coroutine, path, "java", "-Xms256M", "-Xmx512M", "-jar", jar.name, "-o", "FALSE"),
            EnumKServerState.STOPPED
        ).apply {
            onMessage = { message ->
                runBlocking {
                    Katan.webSocket.outgoing.send(Frame.Text(mapOf(
                        "type" to "server-log",
                        "server" to id,
                        "message" to message
                    ).asJsonString()))
                }
            }
        }

        servers.add(server)
        return server
    }

}