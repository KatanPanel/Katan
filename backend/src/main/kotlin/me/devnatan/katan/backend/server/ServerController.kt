package me.devnatan.katan.backend.server

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.sendBlocking
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerPath
import me.devnatan.katan.backend.Katan
import me.devnatan.katan.backend.impl.server.ServerImpl
import me.devnatan.katan.backend.sql.server.ServerEntity
import me.devnatan.katan.backend.util.asJsonString
import me.devnatan.katan.backend.util.createProcess
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.CopyOnWriteArraySet

class ServerController(private val katan: Katan) {

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
    val servers = CopyOnWriteArraySet<Server>()

    fun getServer(id: Int): Server? = servers.find { it.id == id }
    fun getServer(name: String): Server? = servers.find { it.name.equals(name, true) }

    fun load() {
        transaction {
            ServerEntity.all().forEach {
                servers.add(loadServer(it))
            }
        }

        idCounter.value = servers.last().id
        LOGGER.info("Loaded ${servers.size} servers.")
    }

    fun loadServer(server: ServerEntity): Server? {
        val folder = File(serversFolder, "server${server.serverId}")
        if (folder.name.startsWith("-")) {
            LOGGER.info("Ignoring server \"${server.name}\".")
            return null
        }

        if (!folder.exists()) {
            LOGGER.error("Server folder of \"${server.name}\" doesn't exists.")
            return null
        }

        if (!folder.canRead()) {
            LOGGER.error("Access denied to read files in \"${server.name}\".")
            return null
        }

        val kserver = ServerImpl(server.serverId, server.name, ServerPath(server.pathRoot, server.jarFile)).apply {
            process = createProcess(folder, server.initParams)
            process.handler.onMessage = { message ->
                katan.actor.sendBlocking(
                    mapOf(
                        "type" to "server-log",
                        "server" to id,
                        "message" to message
                    ).asJsonString()!!
                )
            }
            initParams = server.initParams
        }

        servers.add(kserver)
        return kserver
    }

    fun createServer(name: String, address: String, port: Int, memory: Int): Int {
        val id = idCounter.incrementAndGet()
        val file = copyDefaultServerFolder(serversFolder, "server$id")
        val server = transaction {
            ServerEntity.new {
                this.serverId = id
                this.name = name
                this.address = address
                this.port = port
                this.pathRoot = file.absolutePath
                this.jarFile = file.listFiles().firstOrNull {
                    it.name.endsWith(".jar")
                }!!.name
                this.initParams = "java -Xms128M -Xmx${memory}M -jar ${this.jarFile} -o FALSE"
            }
        }
        loadServer(server)
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