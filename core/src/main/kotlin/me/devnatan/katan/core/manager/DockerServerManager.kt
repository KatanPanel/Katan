package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.*
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerInspection
import me.devnatan.katan.api.server.ServerState
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.impl.server.DockerServerContainerInspection
import me.devnatan.katan.core.repository.ServersRepository
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.regex.Pattern

class DockerServerManager(
    private val core: Katan,
    private val repository: ServersRepository,
) : ServerManager {

    companion object {

        val logger = LoggerFactory.getLogger(DockerServerManager::class.java)!!

    }

    private object Docker {

        private const val FORMAT = "Katan-%s-%s"
        private val PATTERN = Pattern.compile("Katan\\-(\\d+)\\-([a-z]+)")

        fun asKatanContainer(id: String): String {
            return PATTERN.pattern().format(id)
        }

        fun isKatanContainer(id: String): Boolean {
            return PATTERN.matcher(id).matches()
        }

    }

    private val lastId: AtomicInt
    private val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName("Katan::SM"))
    private val servers: MutableSet<Server> = hashSetOf()

    init {
        suspend {
            logger.info("Loading servers...")
            for (server in repository.listServers()) {
                servers.add(server)

                // We do an initial inspection to ensure that the server will have a container.
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

    override fun getServer(id: Int): Server {
        return servers.first { it.id == id }
    }

    override fun addServer(server: Server): Boolean {
        return servers.add(server)
    }

    override suspend fun registerServer(server: Server) {
        repository.insertServer(server)
    }

    override suspend fun startServer(server: Server) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("Katan::SM-#startServer-${server.container.id}")
    ) {
        core.docker.startContainerCmd(server.container.id).exec()
    }.join()

    override suspend fun stopServer(server: Server, killAfter: Duration) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("Katan::SM-#stopServer-${server.container.id}")
    ) {
        core.docker.stopContainerCmd(server.container.id).withTimeout(killAfter.seconds.toInt()).exec()
    }.join()

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
        }).exec(object : ResultCallback.Adapter<Frame>() {
            override fun onNext(frame: Frame) {
                defer.complete(frame)
                close()
            }
        })
        return defer
    }

    override suspend fun inspectServer(server: Server) {
        suspendCancellableCoroutine<ServerInspection> {
            try {
                val response = core.docker.inspectContainerCmd(server.container.id).exec()

                // update server state
                server.state = response.state.run {
                    when {
                        dead ?: false -> ServerState.DEAD
                        running ?: false -> ServerState.RUNNING
                        paused ?: false -> ServerState.PAUSED
                        restarting ?: false -> ServerState.RESTARTING
                        else -> ServerState.UNKNOWN
                    }
                }

                it.resumeWith(Result.success(DockerServerContainerInspection(response)))
            } catch (e: Throwable) {
                it.cancel(e)
            }
        }
    }

    override suspend fun queryServer(server: Server) {
        throw NotImplementedError()
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