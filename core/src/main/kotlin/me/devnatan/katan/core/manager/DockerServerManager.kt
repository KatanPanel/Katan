package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.*
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainer
import me.devnatan.katan.api.server.ServerInspection
import me.devnatan.katan.api.server.ServerState
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.DockerServerContainerInspection
import me.devnatan.katan.core.repository.ServersRepository
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.regex.Pattern

class DockerServerManager(
    private val core: KatanCore,
    private val repository: ServersRepository,
) : ServerManager {

    companion object {

        const val SCOPE_NAME = "Katan::ServerManager"
        val logger = LoggerFactory.getLogger(ServerManager::class.java)!!

    }

    private object Docker {

        const val VOLUME_ENTRY_POINT = "/Katan/servers"
        private val PATTERN = Pattern.compile("Katan\\-(\\d+)\\-([a-z]+)")

        fun asKatanContainer(id: String): String {
            return PATTERN.pattern().format(id)
        }

        fun isKatanContainer(id: String): Boolean {
            return PATTERN.matcher(id).matches()
        }

    }

    private val lastId: AtomicInt
    private val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName(SCOPE_NAME))
    private val servers: MutableSet<Server> = hashSetOf()

    init {
        suspend {
            logger.info("Loading servers...")
            for (server in repository.listServers()) {
                servers.add(server)

                // We do an initial inspection to ensure that the server will have a container.
                val inspection =
                    coroutineScope.launch(Dispatchers.IO + CoroutineName("$SCOPE_NAME-${server.container.id}")) {
                        inspectServer(server)
                    }

                inspection.invokeOnCompletion {
                    when (it) {
                        is NotFoundException -> {
                            logger.warn(
                                "Server container {} not found, creating a new one with the default settings.",
                                server.name
                            )
                            createServer0(server)
                            return@invokeOnCompletion
                        }

                        // We will not interrupt the loading, the container can be inspected later.
                        else -> {
                            logger.error(
                                "It was not possible to inspect the {} container.",
                                server.container.id,
                                server.name
                            )
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

    override fun getServerList(): Collection<Server> {
        return synchronized (servers) {
            servers.toCollection(hashSetOf())
        }
    }

    override fun getServer(id: Int): Server {
        return servers.first { it.id == id }
    }

    override fun addServer(server: Server): Boolean {
        return servers.add(server)
    }

    override suspend fun createServer(server: Server) {
        coroutineScope.launch(
            Dispatchers.IO + CoroutineName("$SCOPE_NAME-#createServer-${server.container.id}")
        ) {
            createServer0(server)
        }.join()
    }

    private fun createServer0(server: Server) {
        server.container = ServerContainer(core.docker.createContainerCmd("itzg/minecraft-server:multiarch")
            .withCmd("-v", Docker.VOLUME_ENTRY_POINT)
            .withName(Docker.asKatanContainer(server.id.toString()))
            .withHostConfig(HostConfig.newHostConfig().withPortBindings(Ports().apply {
                add(PortBinding(Ports.Binding.bindIpAndPort(server.address, server.port), ExposedPort.tcp(server.port)))
            }))
            .withEnv("EULA=true", "TYPE=SPIGOT", "VERSION=1.8")
            .exec().id)
    }

    override suspend fun registerServer(server: Server) {
        repository.insertServer(server)
    }

    override suspend fun startServer(server: Server) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("$SCOPE_NAME-#startServer-${server.id}")
    ) {
        core.docker.startContainerCmd(server.container.id).exec()
    }.join()

    override suspend fun stopServer(server: Server, killAfter: Duration) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("$SCOPE_NAME-#stopServer-${server.id}")
    ) {
        core.docker.stopContainerCmd(server.container.id).withTimeout(killAfter.seconds.toInt()).exec()
    }.join()

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun runServer(server: Server, command: String): Flow<String> = callbackFlow {
        val callback = object : ResultCallback.Adapter<Frame>() {
            override fun onNext(frame: Frame) {
                coroutineScope.launch(Dispatchers.Default) {
                    send(frame.toString())
                }
            }

            override fun onError(error: Throwable) {
                super.onError(error)
                channel.close(error)
            }
        }

        core.docker.execStartCmd(
            coroutineScope.async(
                Dispatchers.IO + CoroutineName("$SCOPE_NAME-#runServer-${server.id}"),
                CoroutineStart.LAZY
            ) {
                core.docker.execCreateCmd(server.container.id).withCmd(command).exec().id
            }.await()
        ).exec(callback)

        awaitClose {
            callback.close()
        }
    }

}