package me.devnatan.katan.core.impl.server

import br.com.devsrsouza.kotlin.docker.apis.ContainerApi
import br.com.devsrsouza.kotlin.docker.infrastructure.OctetByteArray
import br.com.devsrsouza.kotlin.docker.infrastructure.RequestConfig
import br.com.devsrsouza.kotlin.docker.infrastructure.RequestMethod
import br.com.devsrsouza.kotlin.docker.utils.streaming.requestStreaming
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.Statistics
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import me.devnatan.katan.api.event.server.*
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.server.*
import me.devnatan.katan.common.impl.server.ServerCompositionsImpl
import me.devnatan.katan.common.impl.server.ServerGameImpl
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.compositions.DockerImageServerCompositionFactory
import me.devnatan.katan.core.repository.ServersRepository
import me.devnatan.katan.core.util.attachResultCallback
import me.devnatan.katan.core.util.deferredResultCallback
import org.slf4j.Logger
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.Duration

class DockerServerManager(
    private val core: KatanCore,
    private val repository: ServersRepository,
) : ServerManager {

    companion object {

        const val CONTAINER_NAME_PATTERN = "katan_server_%s"
        const val NETWORK_ID = "katan0"
        private val logger: Logger = logger<DockerServerManager>()

    }

    private val lastId: AtomicInt = atomic(0)
    private val servers: MutableSet<Server> = hashSetOf()
    private val compositionFactories: MutableList<ServerCompositionFactory> =
        arrayListOf()
    private val localDataManager = ServerLocalDataManager(core)
    private val containerApi = ContainerApi(serializer = Json {
        ignoreUnknownKeys = true
    })

    init {
        registerCompositionFactory(DockerImageServerCompositionFactory(core))
    }

    internal suspend fun loadServers() {
        try {
            core.docker.client.inspectNetworkCmd().withNetworkId(NETWORK_ID)
                .exec()
        } catch (e: NotFoundException) {
            core.docker.client.createNetworkCmd().withName(NETWORK_ID)
                .withAttachable(false)
                .exec()
        }

        try {
            repository.listServers { entities ->
                for (entity in entities) {
                    val game = core.gameManager.getGame(entity.gameType)!!
                    val server = ServerImpl(
                        entity.id.value,
                        entity.name,
                        ServerGameImpl(game, entity.gameVersion?.let {
                            game.versions.find { it.name == entity.gameVersion }
                        }),
                        ServerCompositionsImpl(),
                        entity.host,
                        entity.port.toShort()
                    ).apply {
                        container = DockerServerContainer(
                            entity.containerId,
                            CONTAINER_NAME_PATTERN.format(this.id),
                            core.docker.client
                        )
                    }

                    server.holders.addAll(entity.holders.mapNotNull {
                        /*
                        If the account is null this is a database synchronization error
                        we can ignore this, but in the future we should alert that kind of thing.
                     */
                        core.accountManager.getAccount(it.account.value.toString())
                    }.map { ServerHolderImpl(it, server) })

                    for (composition in entity.compositions) {
                        val factory = compositionFactories.firstOrNull {
                            it[composition.key] != null
                        }

                        if (factory == null) {
                            logger.warn("${server.name}: No factory found for composition ${composition.key}, skipping.")
                            continue
                        }

                        val optionsFile =
                            localDataManager.getCompositionOptions(
                                server,
                                composition.key
                            )
                        if (!optionsFile.exists()) {
                            logger.warn("${server.name}: No options found for ${composition.key}, skipping.")
                            continue
                        }

                        @Suppress("UNCHECKED_CAST", "BlockingMethodInNonBlockingContext")
                        val optionsData =
                            FileInputStream(optionsFile).use {
                                core.objectMapper.readValue(it, Map::class.java)
                            } as Map<String, Any>

                        val key = factory[composition.key]!!
                        val impl = factory.create(
                            key,
                            factory.generate(key, optionsData)
                        )
                        impl.read(server)
                        (server.compositions as ServerCompositionsImpl)[key] =
                            impl
                    }

                    try {
                        inspectServer(server)
                    } catch (e: NotFoundException) {
                        logger.warn("Server \"${server.name}\" container was not found.")
                        server.state = ServerState.UNLOADED
                    }

                    // Ensuring that the next id is after the id of any server
                    // already registered this will prevent future collisions.
                    lastId.lazySet(server.id)
                    servers.add(server)
                }
            }

            if (servers.isNotEmpty())
                logger.debug("${servers.size} server(s) have been loaded.")
        } catch (e: Throwable) {
            // if it's SQLErrorSyntaxException probably the Katan version is different
            throw e
        }
    }

    override fun getServerList(): Collection<Server> {
        return servers.toList()
    }

    override fun getServer(id: Int): Server {
        return servers.first { it.id == id }
    }

    override fun getServer(name: String): Server {
        return servers.first { it.name.equals(name, true) }
    }

    override fun addServer(server: Server): Boolean {
        return synchronized(servers) {
            servers.add(server)
        }
    }

    override fun existsServer(id: Int): Boolean {
        return servers.any { it.id == id }
    }

    override fun existsServer(name: String): Boolean {
        return servers.any { it.name.equals(name, true) }
    }

    override suspend fun prepareServer(
        name: String,
        game: ServerGame,
        host: String,
        port: Short
    ): Server {
        return ServerImpl(
            lastId.incrementAndGet(),
            name,
            game,
            ServerCompositionsImpl(),
            host,
            port
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun createServer(server: Server) {
        (server as ServerImpl).container =
            UnresolvedServerContainer(CONTAINER_NAME_PATTERN.format(server.id))
        for (composition in server.compositions) {
            composition.write(server)
        }

        for (composition in server.compositions) {
            OutputStreamWriter(
                FileOutputStream(localDataManager.getCompositionOptions(
                    server, composition.factory[composition.key]!!
                ).apply {
                    createNewFile()
                }), Charsets.UTF_8
            ).use { core.objectMapper.writeValue(it, composition.options) }
        }

        core.eventBus.publish(ServerCreateEvent(server))
    }

    override suspend fun registerServer(server: Server) {
        repository.insertServer(server)
        logger.debug("Server ${server.name} registered.")
    }

    override suspend fun startServer(server: Server) {
        try {
            core.eventBus.publish(ServerPreStartEvent(server))
            server.container.start()
        } catch (ignored: NotModifiedException) {
        }
    }

    override suspend fun stopServer(server: Server) {
        try {
            core.eventBus.publish(ServerPreStopEvent(server))
            server.container.stop()
        } catch (ignored: NotModifiedException) {
        }
    }

    override suspend fun stopServer(server: Server, killAfter: Duration) {
        check(server.container is DockerServerContainer) {
            "Timeout is only supported in Docker containers."
        }

        try {
            core.eventBus.publish(ServerPreStopEvent(server))
            (server.container as DockerServerContainer).stop(killAfter
                .seconds.toInt())
        } catch (ignored: NotModifiedException) {
        }
    }

    override suspend fun inspectServer(server: Server) {
        val response =
            core.docker.client.inspectContainerCmd(server.container.id).exec()
        val inspection = DockerServerContainerInspection(response)
        core.eventBus.publish(ServerInspectedEvent(server, result = inspection))
        server.container.inspection = inspection

        val state = response.state.run {
            when {
                dead ?: false -> ServerState.DEAD
                running ?: false -> ServerState.RUNNING
                paused ?: false -> ServerState.PAUSED
                restarting ?: false -> ServerState.RESTARTING
                else -> ServerState.UNKNOWN
            }
        }

        core.eventBus.publish(
            ServerStateChangeEvent(
                server,
                server.state,
                state
            )
        )
        server.state = state
        logger.debug("Server ${server.name} updated.")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun runServerCommand(
        server: Server,
        command: String,
        options: ServerCommandOptions
    ): Flow<String> = callbackFlow {
        val exec = core.docker.client.execCreateCmd(server.container.id)
            .withCmd(command)
            .withPrivileged(options.privilegied)
            .withWorkingDir(options.wkdir)
            .withEnv(options.env.map { (k, v) -> "$k=$v" })
            .withAttachStderr(false)
            .withAttachStdin(false)
            .withAttachStdout(false)
            .withUser(options.user)
            .withTty(options.tty)
            .exec().id

        core.docker.client.execStartCmd(exec).withDetach(true)
            .exec(attachResultCallback<Frame, String> {
                it.payload.decodeToString()
            })
        awaitClose()
    }

    private fun statisticsToServerStats(statistics: Statistics): ServerStats {
        val pid = statistics.pidsStats.current ?: error("Null pid")
        val mem = statistics.memoryStats!!
        val cpu = statistics.cpuStats!!
        val last = statistics.preCpuStats

        return ServerStatsImpl(
            pid,
            mem.usage!!,
            mem.maxUsage!!,
            mem.limit!!,
            mem.stats!!.cache!!,
            cpu.cpuUsage!!.totalUsage!!,
            cpu.cpuUsage!!.percpuUsage!!.toLongArray(),
            cpu.systemCpuUsage!!,
            cpu.onlineCpus!!,
            last.cpuUsage?.totalUsage,
            last.cpuUsage?.percpuUsage?.toLongArray(),
            last.systemCpuUsage,
            last.onlineCpus
        )
    }

    override suspend fun getServerStats(server: Server): ServerStats {
        val job = deferredResultCallback<Statistics, ServerStats> {
            statisticsToServerStats(it)
        }

        core.docker.client.statsCmd(server.container.id).withNoStream(true)
            .exec(job)
        return job.await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun receiveServerStats(server: Server): Flow<ServerStats> =
        callbackFlow {
            core.docker.client.statsCmd(server.container.id).withNoStream(false)
                .exec(attachResultCallback<Statistics, ServerStats> {
                    statisticsToServerStats(it)
                })

            awaitClose()
        }

    /* @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun receiveServerLogs(server: Server): Flow<String> =
        callbackFlow {
            core.docker.client.logContainerCmd(server.container.id)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(false)
                .withTimestamps(true)
                .exec(attachResultCallback<Frame, String> {
                    it.payload.decodeToString()
                })

            awaitClose()
        } */

    override suspend fun receiveServerLogs(server: Server): Flow<String> {
        return containerApi.containerLogs(
            id = server.container.id,
            follow = false,
            stdout = true,
            stderr = true,
            since = null,
            until = null,
            timestamps = true,
            tail = null
        )
    }

    override fun getCompositionFactory(key: ServerComposition.Key<*>): ServerCompositionFactory? {
        return compositionFactories.firstOrNull { factory ->
            factory.registrations.any { it.value == key }
        }
    }

    override fun getCompositionFactory(name: String): ServerCompositionFactory? {
        return compositionFactories.firstOrNull { factory ->
            factory[name] != null
        }
    }

    override fun registerCompositionFactory(factory: ServerCompositionFactory) {
        synchronized(compositionFactories) {
            compositionFactories.add(factory)
        }
    }

    override fun unregisterCompositionFactory(factory: ServerCompositionFactory) {
        synchronized(compositionFactories) {
            compositionFactories.remove(factory)
        }
    }

}