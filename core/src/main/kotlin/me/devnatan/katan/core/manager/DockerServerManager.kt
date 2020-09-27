package me.devnatan.katan.core.manager

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.Frame
import de.gesellix.docker.compose.ComposeFileReader
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.devnatan.katan.api.manager.ServerManager
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerState
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.repository.ServersRepository
import me.devnatan.katan.core.server.DockerServerContainer
import me.devnatan.katan.core.server.DockerServerContainerInspection
import me.devnatan.katan.core.server.ServerImpl
import me.devnatan.katan.docker.DockerCompose
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
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

    init {
        Files.createDirectories(Paths.get(COMPOSE_ROOT))

        runBlocking {
            logger.info("Loading servers...")
            for (server in repository.listServers()) {
                server.container = DockerServerContainer(server.container.id, core.docker)

                try {
                    inspectServer(server)
                    servers.add(server)
                } catch (e: NotFoundException) {
                    logger.warn("Server \"${server.name}\" container could not be found.")
                    logger.warn("It will not be initialized, execute the build command to build the container and start again.")
                }

                // Ensuring that the next id is after the id of any server
                // already registered this will prevent future collisions.
                lastId.lazySet(server.id)
            }
        }
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

    override suspend fun createServer(server: Server, properties: Map<String, String>): Server {
        val initialized = ServerImpl(lastId.incrementAndGet(),
            server.name,
            server.address,
            server.port,
            server.composition)

        val pwd = File(COMPOSE_ROOT + File.separator + initialized.composition)
        if (!pwd.exists())
            pwd.mkdirs()

        val composeFile = File(pwd, "docker-compose.yml")
        if (!composeFile.exists())
            throw FileNotFoundException("Couldn't find Docker Compose for ${initialized.name} @ ${composeFile.absolutePath}")

        logger.info("Using \"${initialized.composition}\" Docker Compose as server composition.")
        return withContext(
            coroutineScope.coroutineContext + Dispatchers.IO + CoroutineName("$COROUTINE_SCOPE_NAME-#createServer-${server.name}")
        ) {
            val fileEnv = mapOf(
                "KATAN_PORT" to server.port.toString()
            )

            val compose = ComposeFileReader().load(
                FileInputStream(composeFile), pwd.absolutePath, fileEnv
            )!!

            val environmentArgs = properties.map { (key, value) ->
                "-e $key=$value"
            }.joinToString(" ")

            val containerName = CONTAINER_NAME_PATTERN.format(initialized.id.toString())
            for ((serviceName, _) in compose.services ?: emptyMap()) {
                logger.info("Building service \"$serviceName\"...")

                composer.runCommand(
                    "run -d --name $containerName $environmentArgs $serviceName", mapOf(
                        DockerCompose.COMPOSE_FILE to composeFile.absolutePath,
                        DockerCompose.COMPOSE_PROJECT to containerName
                    ), showOutput = false, showErrors = false
                )
            }

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

}