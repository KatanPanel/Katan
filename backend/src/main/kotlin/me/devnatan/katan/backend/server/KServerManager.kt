package me.devnatan.katan.backend.server

import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.backend.io.createProcess
import org.slf4j.Logger
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class KServerManager(private val logger: Logger) {

    private val servers = ConcurrentSet<KServer>()

    fun getServers(): Set<KServer> {
        return Collections.unmodifiableSet(servers)
    }

    fun getServer(id: String): KServer? = servers.find { it.id.equals(id, true) }

    fun load(coroutine: CoroutineScope) {
        val folder = File("C:\\Users\\GFIRE\\Documents\\CloudCraft\\Katan\\backend\\src\\main\\resources\\servers")
        if (!folder.exists())
            folder.mkdir()

        for (serverPath in folder.listFiles()) {
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
        logger.info("[+] Loaded ${servers.size} servers.")
    }

    fun loadServer(coroutine: CoroutineScope, id: String, path: File): KServer {
        val jar = path.listFiles().firstOrNull {
            it.name == "PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar"
        } ?: throw FileNotFoundException("Couldn't find PaperSpigot for server \"$id\".")

        val server = KServer(
            id,
            KServerPath(path.absolutePath, jar.name),
            createProcess(coroutine, path, "java", "-Xms256M", "-Xmx512M", "-jar", jar.name, "-o", "FALSE"),
            EnumKServerState.STOPPED
        )
        servers.add(server)
        return server
    }

}