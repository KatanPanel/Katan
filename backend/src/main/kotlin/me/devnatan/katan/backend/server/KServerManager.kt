package me.devnatan.katan.backend.server

import io.netty.util.internal.ConcurrentSet
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.sendBlocking
import me.devnatan.katan.backend.Katan
import me.devnatan.katan.backend.io.createProcess
import me.devnatan.katan.backend.sql.Server
import me.devnatan.katan.backend.util.asJsonString
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException

class KServerManager(private val katan: Katan) {

    companion object {

        private val LOGGER = LoggerFactory.getLogger("ServerManager")!!

    }

    private val serversFolder: File by lazy {
        val serversFolder = File(this::class.java.classLoader.getResource("servers").file)
        if (!serversFolder.exists())
            serversFolder.mkdir()
        serversFolder
    }
    private val idCounter: AtomicInt = atomic(0)
    val servers: ConcurrentSet<KServer> = ConcurrentSet()

    fun getServer(id: Int): KServer? = servers.find { it.id == id }
    fun getServer(name: String): KServer? = servers.find { it.name.equals(name, true) }

    fun load(coroutine: CoroutineScope) {
        transaction {
            Server.all().forEach {
                servers.add(loadServer(coroutine, it))
            }
        }

        idCounter.value = servers.size
        LOGGER.info("Loaded ${servers.size} servers.")
    }

    fun loadServer(coroutine: CoroutineScope, server: Server): KServer? {
        val folder = File(serversFolder, "server${server.serverId}")
        if (folder.name.startsWith("-")) {
            LOGGER.info("Ignoring server \"${server.name}\".")
            return null
        }

        if (!folder.exists()) {
            LOGGER.error("Access denied to read files in \"${server.name}\".")
            return null
        }

        if (!folder.canRead()) {
            LOGGER.error("Access denied to read files in \"${server.name}\".")
            return null
        }

        val kserver = KServer(server.serverId, server.name,
            KServerPath(server.pathRoot, server.jarFile),
            createProcess(coroutine, folder, server.initParams),
            EnumKServerState.STOPPED,
            KServerQuery.offline(),
            server.initParams
        ).apply {
            onMessage = { message ->
                katan.actor.sendBlocking(
                    mapOf(
                        "type" to "server-log",
                        "server" to id,
                        "message" to message
                    ).asJsonString()!!
                )
            }
        }
        servers.add(kserver)
        return kserver
    }

    fun createServer(coroutine: CoroutineScope, name: String, address: String, port: Int, memory: Int): Int {
        val id = idCounter.incrementAndGet()
        val file = copyDefaultServerFolder(serversFolder, "server$id")
        val server = transaction {
            Server.new {
                this.serverId = id
                this.name = name
                this.address = address
                this.port = port
                this.pathRoot = file.absolutePath
                this.jarFile = file.listFiles().firstOrNull {
                    it.name.endsWith(".jar")
                }!!.name
                this.initParams = "java -Xms1024M -Xmx${memory}M -jar ${this.jarFile} -o FALSE PAUSE"
            }
        }
        loadServer(coroutine, server)
        return id
    }

    private fun copyDefaultServerFolder(path: File, to: String): File {
        val default = File(path, "-default")
        if (!default.exists())
            throw FileNotFoundException("Server default folder doesn't exists")

        val file = File(path, to)
        default.copyRecursively(file)
        return file
    }
}