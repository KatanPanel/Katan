package me.devnatan.katan.backend.server

import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.sendBlocking
import me.devnatan.katan.backend.Katan
import me.devnatan.katan.backend.io.createProcess
import me.devnatan.katan.backend.util.asJsonString
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException

class KServerManager(private val katan: Katan) {

    companion object {

        private val LOGGER = LoggerFactory.getLogger("ServerManager")!!

    }

    val servers: ConcurrentSet<KServer> = ConcurrentSet()

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
                LOGGER.info("Ignoring server \"$serverId\".")
                continue
            }

            if (!serverPath.canRead()) {
                LOGGER.error("Access denied to read files in \"$serverId\".")
                continue
            }

            loadServer(coroutine, serverId, serverPath)
        }
        LOGGER.info("Loaded ${servers.size} servers.")
    }

    fun loadServer(coroutine: CoroutineScope, id: String, path: File): KServer {
        val jar = path.listFiles().firstOrNull {
            it.name.endsWith(".jar")
        } ?: throw FileNotFoundException("Couldn't find PaperSpigot for server \"$id\".")

        val server = KServer(
            id,
            KServerPath(path.absolutePath, jar.name),
            createProcess(coroutine, path, "java", "-Xms256M", "-Xmx512M", "-jar", jar.name, "-o", "FALSE"),
            EnumKServerState.STOPPED,
            KServerQuery.offline()
        ).apply {
            onMessage = { message ->
                katan.actor.sendBlocking(mapOf(
                    "type" to "server-log",
                    "server" to id,
                    "message" to message
                ).asJsonString())
            }
        }

        servers.add(server)
        return server
    }

}