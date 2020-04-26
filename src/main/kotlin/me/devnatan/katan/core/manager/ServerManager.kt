package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.Ports
import io.ktor.http.cio.websocket.WebSocketSession
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.isActive
import me.devnatan.katan.Katan
import me.devnatan.katan.api.io.websocket.KWSSession
import me.devnatan.katan.api.server.KServer
import me.devnatan.katan.api.server.KServerContainer
import me.devnatan.katan.api.server.KServerQuery
import me.devnatan.katan.api.server.address
import me.devnatan.katan.core.impl.server.KServerImpl
import me.devnatan.katan.core.sql.ServerEntity
import me.devnatan.katan.core.util.ServerQuery
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

@ExperimentalUnsignedTypes
class ServerManager(private val core: Katan) {

    private val logger  = LoggerFactory.getLogger(ServerManager::class.java)!!
    private val lastId  = atomic(0)
    private val servers = ConcurrentHashMap.newKeySet<KServer>()!!

    /**
     * Returns all currently registered servers.
     */
    fun getServers(): Set<KServer> {
        return servers
    }

    /**
     * Returns a server that contains the specified id.
     * @param id server id
     * @throws IllegalArgumentException if the server is not found
     */
    @Throws(IllegalArgumentException::class)
    fun getServer(id: UInt): KServer {
        return servers.find { it.id == id } ?: throw IllegalArgumentException(id.toString())
    }

    /**
     * Create and register a new server with the specified name and port.
     * @param name server name
     * @param port remote server port
     */
    fun createServer(name: String, port: Short): KServer {
        // if there is a server with the same port it returns the same
        servers.find { it.port == port }?.let {
            return it
        }

        val id = lastId.incrementAndGet()
        val containerId = "katan-server-$id"
        createContainer(containerId, port.toInt())
        transaction {
            ServerEntity.new(id) {
                this.name = name
                this.port = port.toInt()
                this.containerId = containerId
            }
        }

        val server = KServerImpl(id.toUInt(), name, port, KServerContainer(containerId), KServerQuery.Empty)
        inspectServer(server)
        servers.add(server)
        return server
    }

    /**
     * Starts the server that has the specified id.
     * @param id server id
     * @throws IllegalArgumentException if the server is not found
     */
    @Throws(IllegalArgumentException::class)
    fun startServer(id: UInt): KServer {
        val server = getServer(id)
        core.docker.startContainerCmd(server.container.id).exec()
        return inspectServer(server)
    }

    /**
     * Stops the server that has the specified id.
     * @param id server id
     * @throws IllegalArgumentException if the server is not found
     */
    @Throws(IllegalArgumentException::class)
    fun stopServer(id: UInt): KServer {
        val server = getServer(id)
        core.docker.stopContainerCmd(server.container.id).exec()
        return inspectServer(server)
    }

    /**
     * Executes the [input] command on the internal server process with the specified id.
     * @param id    server id
     * @param input the command
     * @throws IllegalArgumentException if the server is not found
     */
    @Throws(IllegalArgumentException::class)
    fun logServer(id: UInt, input: String) {
        core.docker.execCreateCmd(getServer(id).container.id).withCmd(*input.split(" ").toTypedArray()).exec()
    }

    /**
     * Attach the session to the server records that
     * have the specified id by calling [block] to each result.
     * @param id        server id
     * @param session   the current websocket session
     * @param block     the executor for each result
     * @throws IllegalArgumentException if the server is not found
     */
    @Throws(IllegalArgumentException::class)
    fun attachServer(id: UInt, session: WebSocketSession, block: KWSSession.(Frame) -> Unit) {
        core.docker.attachContainerCmd(getServer(id).container.id)
            .withLogs(false)
            .withFollowStream(true)
            .withTimestamps(true)
            .withStdOut(true)
            .withStdErr(true)
            .exec(object: ResultCallback<Frame> {
                lateinit var attach: KWSSession

                override fun onNext(frame: Frame) {
                    if (!session.isActive) {
                        attach.close()
                        return
                    }

                    block(attach, frame)
                }

                override fun onComplete() {}

                override fun onError(error: Throwable) {}

                override fun onStart(stream: Closeable) {
                    attach = KWSSession(session, stream)
                    /**
                     * @TODO ping pong
                     */
                }

                override fun close() {}
            })
    }

    /**
     * Inspects the internal process of the specified server container.
     * @param server the server
     */
    fun inspectServer(server: KServer): KServer {
        val container = server.container
        container.inspection = core.docker.inspectContainerCmd(container.id).exec()
        container.isInspected = true
        return server
    }

    /**
     * Searches through the IP address of the specified server.
     * @param server the server
     */
    fun queryServer(server: KServer): KServer {
        return server.apply {
            ServerQuery.query(InetSocketAddress(server.address, server.port.toInt()))?.let { query = it }
        }
    }

    private fun createContainer(containerId: String, port: Int) {
        val mcport = ExposedPort.tcp(port)
        val ports = Ports()
        ports.bind(mcport, Ports.Binding.bindPort(port))

        core.docker.createContainerCmd("hsorg/katan-mc-image")
            .withName(containerId)
            .withExposedPorts(mcport)
            .withPortBindings(ports)
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withStdinOpen(true)
            .withTty(true)
            .withEnv("EULA=true", "ENABLE_RCON=false")
            .exec()
    }

    internal fun loadServers() {
        transaction {
            for (server in ServerEntity.all()) {
                lastId.value = server.id.value
                val impl = KServerImpl(server.id.value.toUInt(), server.name, server.port.toShort(), KServerContainer(server.containerId), KServerQuery.Empty)
                try {
                    inspectServer(impl)
                } catch (e: NotFoundException) {
                    this@ServerManager.logger.info("Container ${server.containerId} not found, creating a new one...")
                    createContainer(server.containerId, server.port)
                }

                servers.add(impl)
            }
        }

        logger.info("Loaded ${servers.size} servers.")
    }

}