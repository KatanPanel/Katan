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
import me.devnatan.katan.core.server.composition.DockerImageFactory
import me.devnatan.katan.docker.DockerCompose
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration

class DockerServerManager(
    private val core: KatanCore,
    private val repository: ServersRepository,
) : ServerManager {

    companion object {

        const val COROUTINE_SCOPE_NAME = "Katan::ServerManager"
        const val CONTAINER_NAME_PATTERN = "katan_server_%s"
        const val COMPOSE_ROOT = "compose"
        val logger = LoggerFactory.getLogger(ServerManager::class.java)!!

    }

    private val lastId: AtomicInt = atomic(0)
    private val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName(COROUTINE_SCOPE_NAME))
    private val servers: MutableSet<Server> = hashSetOf()
    private val composer = DockerCompose(core.platform, logger)
    private val compositionFactories: MutableList<ServerCompositionFactory> = arrayListOf()

    init {
        logger.info("Compose root is located at: " + Files.createDirectories(Paths.get(COMPOSE_ROOT)))

        runBlocking {
            logger.info("Loading servers...")
            for (server in repository.listServers()) {
                server.container = DockerServerContainer(server.container.id, core.docker)

                try {
                    logger.info("Inspecting server \"${server.name}\"...")
                    inspectServer(server)
                    servers.add(server)
                } catch (e: NotFoundException) {
                    logger.warn("Server \"${server.name}\" server container was not found, it could not be initialized.")
                    logger.warn("Use \"katan server create\" to create a new server or compose one from a Docker Compose file using \"katan server compose\".")
                }

                // Ensuring that the next id is after the id of any server
                // already registered this will prevent future collisions.
                lastId.lazySet(server.id)
            }
            logger.info("${servers.size} servers have been loaded.")
        }

        registerCompositionFactory(DockerImageFactory)
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
        val initialized = ServerImpl(
            lastId.incrementAndGet(),
            server.name,
            server.address,
            server.port
        )

        /* val pwd = File(COMPOSE_ROOT + File.separator + initialized.composition)
        if (!pwd.exists())
            pwd.mkdirs()

        val composeFile = File(pwd, "docker-compose.yml")
        if (!composeFile.exists())
            throw FileNotFoundException("Couldn't find Docker Compose for ${initialized.name} @ ${composeFile.absolutePath}")

        logger.info("Using \"${initialized.composition}\" Docker Compose as server composition.") */
        return withContext(
            Dispatchers.IO + CoroutineName("$COROUTINE_SCOPE_NAME-#createServer-${server.name}")
        ) {
            val fileEnv = mapOf(
                "KATAN_PORT" to server.port.toString()
            )

            /* val compose = ComposeFileReader().load(
                FileInputStream(composeFile), pwd.absolutePath, fileEnv
            )!!

            val environmentArgs = properties.map { (key, value) ->
                "-e $key=$value"
            }.joinToString(" ")

            */
            val containerName = CONTAINER_NAME_PATTERN.format(initialized.id.toString())
            /*for ((serviceName, _) in compose.services ?: emptyMap()) {
                logger.info("Building service \"$serviceName\"...")

                composer.runCommand(
                    "run -d --name $containerName $environmentArgs $serviceName", mapOf(
                        DockerCompose.COMPOSE_FILE to composeFile.absolutePath,
                        DockerCompose.COMPOSE_PROJECT to containerName
                    ), showOutput = false, showErrors = false
                )
            } */

            logger.info("Obtaining the server container identification number...")
            initialized.apply {
                container = DockerServerContainer(containerName, core.docker)
            }
        }
    }

    override suspend fun registerServer(server: Server) {
        repository.insertServer(server)
    }

    override suspend fun startServer(server: Server) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("$COROUTINE_SCOPE_NAME-#startServer-${server.id}")
    ) {
        server.container.start()
    }.join()

    override suspend fun stopServer(server: Server, killAfter: Duration) = coroutineScope.launch(
        Dispatchers.IO + CoroutineName("$COROUTINE_SCOPE_NAME-#stopServer-${server.id}")
    ) {
        server.container.stop()
    }.join()

    override suspend fun inspectServer(server: Server) {
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

        server.container.inspection = DockerServerContainerInspection(response)
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
        logger.info(
            "Composition factory registered for ${
                factory.applicable.joinToString {
                    it.name
                }
            }."
        )
    }

    override fun unregisterCompositionFactory(factory: ServerCompositionFactory) {
        compositionFactories.remove(factory)
        logger.info(
            "Unregistered ${
                factory.applicable.joinToString {
                    it.toString()
                }
            } composition factory."
        )
    }

}