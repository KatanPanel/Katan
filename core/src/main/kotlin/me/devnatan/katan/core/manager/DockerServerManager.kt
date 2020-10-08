package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.Frame
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.devnatan.katan.api.event.*
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.api.server.ServerState
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.repository.ServersRepository
import me.devnatan.katan.core.server.DockerServerContainer
import me.devnatan.katan.core.server.DockerServerContainerInspection
import me.devnatan.katan.core.server.ServerImpl
import me.devnatan.katan.core.server.compositions.DockerCompositionFactory
import me.devnatan.katan.docker.DockerCompose
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.time.Duration
import kotlin.system.measureTimeMillis

class DockerServerManager(
    private val core: KatanCore,
    private val repository: ServersRepository,
) : ServerManager {

    companion object {

        const val COROUTINE_SCOPE_NAME = "Katan::ServerManager"
        const val CONTAINER_NAME_PATTERN = "katan_server_%s"
        const val COMPOSE_ROOT = "composer"
        const val NETWORK_ID = "katan0"
        val logger = LoggerFactory.getLogger(ServerManager::class.java)!!

    }

    private val lastId: AtomicInt = atomic(0)
    private val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName(COROUTINE_SCOPE_NAME))
    private val servers: MutableSet<Server> = hashSetOf()
    val composer = DockerCompose(core.platform, logger)
    private val compositionFactories: MutableList<ServerCompositionFactory> = arrayListOf()

    init {
        registerCompositionFactory(DockerCompositionFactory(core))
        runBlocking {
            for (server in repository.listServers()) {
                server.container = DockerServerContainer(server.container.id, core.docker)

                try {
                    inspectServer(server)
                    servers.add(server)
                } catch (e: NotFoundException) {
                    logger.warn("Server \"${server.name}\" container was not found, it could not be initialized.")
                }

                // Ensuring that the next id is after the id of any server
                // already registered this will prevent future collisions.
                lastId.lazySet(server.id)
            }
        }

        File(COMPOSE_ROOT).let { if (!it.exists()) it.mkdirs() }

        try {
            core.docker.inspectNetworkCmd().withNetworkId(NETWORK_ID).exec()
        } catch (e: NotFoundException) {
            core.docker.createNetworkCmd().withName(NETWORK_ID)
                .withAttachable(true)
                .exec()
        }

        if (servers.isNotEmpty())
            logger.debug("${servers.size} servers have been loaded.")
    }

    override fun getServerList(): Collection<Server> {
        return synchronized(servers) {
            servers.toCollection(hashSetOf())
        }
    }

    override fun getServer(id: Int): Server {
        return servers.first { it.id == id }
    }

    override fun getServer(name: String): Server {
        return servers.first { it.name.equals(name, true) }
    }

    override fun addServer(server: Server): Boolean {
        return servers.add(server)
    }

    override fun existsServer(id: Int): Boolean {
        return servers.any { it.id == id }
    }

    override fun existsServer(name: String): Boolean {
        return servers.any { it.name.equals(name, true) }
    }

    override suspend fun createServer(server: Server): Server {
        val impl = ServerImpl(lastId.incrementAndGet(), server.name).apply {
            container = DockerServerContainer(CONTAINER_NAME_PATTERN.format(id), core.docker)
        }

        core.eventBus.publish(ServerCreateEvent(server))
        return impl
    }

    override suspend fun registerServer(server: Server) {
        repository.insertServer(server)
    }

    override suspend fun startServer(server: Server) {
        core.eventBus.publish(ServerBeforeStartEvent(server))
        val duration = Duration.ofMillis(measureTimeMillis {
            server.container.start()
        })
        core.eventBus.publish(ServerStartEvent(server, duration))
    }

    override suspend fun stopServer(server: Server, killAfter: Duration) {
        core.eventBus.publish(ServerBeforeStopEvent(server))
        val duration = Duration.ofMillis(measureTimeMillis {
            server.container.stop()
        })
        core.eventBus.publish(ServerStopEvent(server, duration))
    }

    override suspend fun inspectServer(server: Server) {
        val response = core.docker.inspectContainerCmd(server.container.id).exec()
        val state = response.state.run {
            when {
                dead ?: false -> ServerState.DEAD
                running ?: false -> ServerState.RUNNING
                paused ?: false -> ServerState.PAUSED
                restarting ?: false -> ServerState.RESTARTING
                else -> ServerState.UNKNOWN
            }
        }

        core.eventBus.publish(ServerStateChangeEvent(server, state))
        server.state = state

        val inspection = DockerServerContainerInspection(response)
        core.eventBus.publish(ServerInspectionEvent(server, inspection))
        server.container.inspection = inspection
    }

    // TODO: use conflated channel
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun runServer(server: Server, command: String): Flow<String> = callbackFlow {
        val callback = object : ResultCallback<Frame> {
            private var stream: Closeable? = null

            override fun onNext(frame: Frame) {
                sendBlocking(frame.toString())
            }

            override fun onStart(closeable: Closeable) {
                stream = closeable
            }

            override fun onComplete() {
                channel.close()
            }

            override fun onError(error: Throwable) {
                channel.close(error)
            }

            override fun close() {
                @Suppress("BlockingMethodInNonBlockingContext")
                stream?.close()
            }
        }

        core.docker.execStartCmd(
            coroutineScope.async(
                Dispatchers.IO + CoroutineName("$COROUTINE_SCOPE_NAME-#runServer-${server.id}"),
                CoroutineStart.LAZY
            ) {
                core.docker.execCreateCmd(server.container.id).withCmd(command).exec().id
            }.await()
        ).exec(callback)

        awaitClose {
            callback.close()
        }
    }

    override fun getRegisteredCompositionFactories(): Collection<ServerCompositionFactory> {
        return compositionFactories
    }

    override fun getCompositionFactoryFor(key: ServerComposition.Key<*>): ServerCompositionFactory? {
        return compositionFactories.firstOrNull { factory ->
            factory.applicable.any { it == key }
        }
    }

    override fun getCompositionFactoryApplicableFor(name: String): ServerCompositionFactory? {
        return compositionFactories.firstOrNull { factory ->
            factory.applicable.any { it.name == name }
        }
    }

    override fun registerCompositionFactory(factory: ServerCompositionFactory) {
        compositionFactories.add(factory)
        logger.debug(
            "Composition factory registered for ${
                factory.applicable.joinToString {
                    it.name
                }
            }."
        )
    }

    override fun unregisterCompositionFactory(factory: ServerCompositionFactory) {
        compositionFactories.remove(factory)
        logger.debug(
            "Unregistered composition factory of ${
                factory.applicable.joinToString {
                    it.toString()
                }
            }."
        )
    }

}