package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.Ports
import io.ktor.http.cio.websocket.*
import kotlinx.atomicfu.atomic
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainer
import me.devnatan.katan.api.server.ServerHolder
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.impl.server.ServerHolderImpl
import me.devnatan.katan.core.impl.server.ServerImpl
import me.devnatan.katan.core.sql.dao.AccountsTable
import me.devnatan.katan.core.sql.dao.ServerEntity
import me.devnatan.katan.core.sql.dao.ServerHolderEntity
import me.devnatan.katan.core.sql.dao.ServersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

class ServerManager(private val core: Katan) {

    private companion object {

        const val CONTAINER_NAME_SCHEMA = "katan::server-%d"

        private val logger = LoggerFactory.getLogger(ServerManager::class.java)!!
    }

    private val lastId = atomic(0)
    private val servers = ConcurrentHashMap.newKeySet<Server>()!!

    /**
     * Returns all currently registered servers.
     */
    fun getServers(): Set<Server> {
        return servers
    }

    /**
     * Returns a server that contains the specified id.
     * @param id server id
     * @throws IllegalArgumentException if the server is not found
     */
    @Throws(IllegalArgumentException::class)
    fun getServer(id: Int): Server {
        return servers.find { it.id == id } ?: throw IllegalArgumentException(id.toString())
    }

    /**
     * Create and register a new server with the specified name and port.
     * @param name server name
     * @param address server host name
     * @param port remote server port
     */
    fun createServer(name: String, address: String, port: Short): Server {
        val id = lastId.incrementAndGet()
        val containerId = CONTAINER_NAME_SCHEMA.format(id)

        createContainer(containerId, port.toInt())
        transaction(core.database) {
            ServerEntity.new(id) {
                this.name = name
                this.address = address
                this.port = port.toInt()
                this.containerId = containerId
            }

        }

        val server = ServerImpl(id, name, address, port, ServerContainer(containerId))
        inspectServer(server)
        servers.add(server)
        return server
    }

    /**
     * Adds new holder for the specified server.
     * @param server the server
     * @param holderId holder account id
     * @throws IllegalArgumentException if the holder account doesn't exists
     */
    fun addServerHolder(server: Server, holderId: String): ServerHolder {
        val account = core.accountManager.getAccountById(holderId)
            ?: throw IllegalArgumentException("Account $holderId not found")

        val holder = ServerHolderImpl(account, server)
        transaction(core.database) {
            ServerHolderEntity.new {
                this.account = EntityID(account.id, AccountsTable)
                this.server = EntityID(server.id, ServersTable)
            }
        }
        server.holders.add(holder)
        return holder
    }

    /**
     * Starts the server that has the specified id.
     * @param id server id
     * @throws IllegalArgumentException if the server is not found
     */
    fun startServer(id: Int): Server {
        val server = getServer(id)
        core.docker.startContainerCmd(server.container.id).exec()
        return inspectServer(server)
    }

    /**
     * Stops the server that has the specified id.
     * @param id server id
     * @throws IllegalArgumentException if the server is not found
     */
    fun stopServer(id: Int): Server {
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
    fun logServer(id: Int, input: String) {
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
    fun attachServer(id: Int, session: WebSocketSession, block: WebSocketSession.(Frame) -> Unit) {
        core.docker.attachContainerCmd(getServer(id).container.id)
            .withLogs(false)
            .withFollowStream(true)
            .withTimestamps(true)
            .withStdOut(true)
            .withStdErr(true)
            .exec(object : ResultCallback<Frame> {
                lateinit var attached: WebSocketSession

                override fun onNext(frame: Frame) {}

                override fun onComplete() {}

                override fun onError(error: Throwable) {}

                override fun onStart(stream: Closeable) {
                }

                override fun close() {}
            })
    }

    /**
     * Inspects the internal process of the specified server container.
     * @param server the server
     */
    fun inspectServer(server: Server): Server {
        val container = server.container
        container.inspection = core.docker.inspectContainerCmd(container.id).exec()
        container.isInspected = true
        return server
    }

    private fun createContainer(containerId: String, port: Int) {
        val mcport = ExposedPort.tcp(port)
        val ports = Ports()
        ports.bind(mcport, Ports.Binding.bindPort(port))

        core.docker.createContainerCmd("itzg/minecraft-server:multiarch")
            .withCmd("-v", "/Katan/servers")
            .withName(containerId)
            .withExposedPorts(mcport)
            .withPortBindings(ports)
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withStdinOpen(true)
            .withTty(true)
            .withEnv("EULA=true", "TYPE=SPIGOT", "VERSION=1.8")
            .exec()
    }

    init {
        transaction(core.database) {
            for (server in ServerEntity.all()) {
                lastId.value = server.id.value
                val impl = ServerImpl(server.id.value,
                    server.name,
                    server.address,
                    server.port.toShort(),
                    ServerContainer(server.containerId))
                impl.holders.addAll(server.holders.map {
                    ServerHolderImpl(core.accountManager.getAccountById(it.account.value.toString())!!, getServer(it.server.value))
                }.toMutableList())

                try {
                    inspectServer(impl)
                } catch (e: NotFoundException) {
                    createContainer(server.containerId, server.port)
                }

                servers.add(impl)
            }
        }
    }

}