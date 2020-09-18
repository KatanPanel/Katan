package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.*
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainer
import me.devnatan.katan.api.server.ServerInspection
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.dao.ServerEntity
import me.devnatan.katan.core.impl.server.DockerServerContainerInspection
import me.devnatan.katan.core.impl.server.ServerHolderImpl
import me.devnatan.katan.core.impl.server.ServerImpl
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Duration

class ServerManager(private val core: Katan) {

    companion object {

        val logger = LoggerFactory.getLogger(ServerManager::class.java)!!

    }

    private val lastId: AtomicInt
    private val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName("Katan::SM"))
    private val servers: MutableSet<Server> = hashSetOf()

    init {
        logger.info("Loading servers...")
        transaction(core.database) {
            ServerEntity.all().forEach { entity ->
                val server = ServerImpl(entity.id.value,
                    entity.name,
                    entity.address,
                    entity.port,
                    ServerContainer(entity.containerId))

                server.holders.addAll(entity.holders.mapNotNull {
                    /*
                        If the account is null this is a database synchronization error
                        we can ignore this, but in the future we should alert that kind of thing.
                     */
                    core.accountManager.getAccount(it.account.value.toString())
                }.map {
                    ServerHolderImpl(it, server)
                })

                servers.add(server)

                /*
                    We do an initial inspection to ensure that the server will have a container.
                 */
                val inspection =
                    coroutineScope.launch(Dispatchers.IO + CoroutineName("Katan::SM-II-${server.container.id}")) {
                        inspectServer(server)
                    }

                inspection.invokeOnCompletion {
                    when (it) {
                        is NotFoundException -> {
                            logger.warn("Server container {} not found, creating a new one with the default settings.",
                                server.name)
                            createContainer(server.container.id, server.address, server.port)
                            return@invokeOnCompletion
                        }

                        // We will not interrupt the loading, the container can be inspected later.
                        else -> {
                            logger.error("It was not possible to inspect the {} container.",
                                server.container.id,
                                server.name)
                            logger.error(it.toString())
                        }
                    }
                }
            }
        }

        // Ensuring that the next id is after the id of any server
        // already registered this will prevent future collisions.
        lastId = synchronized(servers) {
            atomic(runCatching {
                servers.maxOf { it.id }
            }.getOrNull() ?: 0)
        }
    }

    /**
     * Returns a server with the specified [id].
     *
     * @param id the server id
     * @throws NoSuchElementException if the server in question does not exist.
     */
    fun getServer(id: Int): Server {
        return servers.first { it.id == id }
    }

    /**
     * Adds a server to the list of available servers.
     *
     * @param server the server to be added
     * @return whether the server was successfully added
     */
    fun addServer(server: Server): Boolean {
        return servers.add(server)
    }

    /**
     * Register a new server in the database.
     *
     * This method does not add the server to the list of available servers, this must be done before that.
     * The server object will be preserved so that there are no synchronization
     * problems due to diverging information such as: inspection.
     *
     * Similar to other methods that involve operations in the database or completely blocking
     * functions, this method will be suspended and will only be resumed at the end of the operation.
     *
     * @param server the server to be registered
     */
    suspend fun registerServerAsync(server: Server): Deferred<Unit> {
        val serverId = lastId.incrementAndGet()
        val containerId = "katan::Server-$serverId".format(serverId)

        return suspendedTransactionAsync(Dispatchers.IO, core.database) {
            ServerEntity.new(server.id) {
                this.name = name
                this.address = address
                this.port = port
                this.containerId = containerId
            }
        }
    }

    /**
     * Starts a server without blocking the current thread.
     *
     * It is possible to know if the server was started successfully
     * through [Job.invokeOnCompletion], or using [Job.join] with try-with-resources directly.
     *
     * @param server the server to be started
     */
    fun startServer(server: Server) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("Katan::SM-#startServer-${server.container.id}")
    ) {
        core.docker.startContainerCmd(server.container.id).exec()
    }

    /**
     * Stops a server without blocking the current thread.
     *
     * It is possible to know if the server was stopped successfully
     * through [Job.invokeOnCompletion], or using [Job.join] with try-with-resources directly.
     *
     * @param server the server to be stopped
     * @param killAfter maximum execution time until force to kill the server (default: 10 seconds)
     */
    fun stopServer(server: Server, killAfter: Duration = Duration.ofSeconds(10)) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("Katan::SM-#stopServer-${server.container.id}")
    ) {
        core.docker.stopContainerCmd(server.container.id).withTimeout(killAfter.seconds.toInt()).exec()
    }

    /**
     * Sends a signal to the server to execute the specified [command].
     *
     * @param server the command to be executed
     * @param input the command
     */
    suspend fun executeServerCommand(server: Server, command: String): Deferred<Frame> {
        val defer = CompletableDeferred<Frame>()
        core.docker.execStartCmd(withContext(Dispatchers.IO + CoroutineName("Katan::SM-#executeServerCommand-${server.container.id}")
        ) {
            core.docker.execCreateCmd(server.container.id).withCmd(*command.split(" ").toTypedArray()).exec().id
        }).exec(object: ResultCallback.Adapter<Frame>() {
            override fun onNext(frame: Frame) {
                defer.complete(frame)
                close()
            }
        })
        return defer
    }

    /**
     * Inspect the specified server container.
     *
     * This is a cancellable process, and it can be asynchronous,
     * this function will be suspended until the inspection operation ends.
     *
     * By the Katan Web server this function can be called via WebSocket or via HTTP request,
     * if the request is HTTP due to its asynchronous nature, there will be no response to the request.
     *
     * The supposed response to the request,
     * which is not guaranteed, must be made through the server's query method.
     *
     * @param server the server to be inspected
     * @return the inspection result.
     */
    suspend fun inspectServer(server: Server) = suspendCancellableCoroutine<ServerInspection> {
        try {
            it.resumeWith(Result.success(DockerServerContainerInspection(core.docker.inspectContainerCmd(server.container.id)
                .exec())))
        } catch (e: Throwable) {
            it.cancel(e)
        }
    }

    /* internal */
    private fun createContainer(containerId: String, host: String, port: Int) {
        core.docker.createContainerCmd("itzg/minecraft-server:multiarch")
            .withCmd("-v", "/Katan/servers")
            .withName(containerId)
            .withHostConfig(HostConfig.newHostConfig().withPortBindings(Ports().apply {
                add(PortBinding(Ports.Binding.bindIpAndPort(host, port), ExposedPort.tcp(port)))
            }))
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withStdinOpen(true)
            .withTty(true)
            .withEnv("EULA=true", "TYPE=SPIGOT", "VERSION=1.8")
            .exec()
    }

}