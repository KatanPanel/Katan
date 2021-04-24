package me.devnatan.katan.api.server

import kotlinx.coroutines.flow.Flow
import me.devnatan.katan.api.composition.Composition
import me.devnatan.katan.api.composition.CompositionFactory
import me.devnatan.katan.api.composition.CompositionOptions
import me.devnatan.katan.api.composition.CompositionStore
import java.time.Duration

/**
 * Responsible for the generation, handling of absolutely
 * everything related to [Server]s, from creation to query.
 */
interface ServerManager {

    /**
     * Returns a copy of all registered servers.
     */
    fun getServerList(): Collection<Server>

    /**
     * Returns a server with the specified [id].
     * @param id the server id.
     * @throws NoSuchElementException if the server not exists.
     */
    fun getServer(id: Int): Server

    /**
     * Returns a server with the specified [name].
     * @param name the server name.
     * @throws NoSuchElementException if the server not exists.
     */
    fun getServer(name: String): Server

    /**
     * Adds a server to the list of available servers.
     * @param server the server to be added
     * @return `true` if the server was successfully added
     */
    fun addServer(server: Server): Boolean

    /**
     * Returns if there is a server registered with the specified id.
     * @param id the server id.
     */
    fun existsServer(id: Int): Boolean

    /**
     * Returns if there is a server registered with the specified [name].
     * @param name the server name.
     */
    fun existsServer(name: String): Boolean

    /**
     * Returns a [Server] generated with the specified [name], [game], [host] and [port].
     * @param name the server name.
     * @param game the server game target.
     * @param host the server remote connection host address.
     * @param port the server remote connection port.
     */
    suspend fun prepareServer(name: String, game: ServerGame, host: String, port: Short): Server

    /**
     * Creates a previously prepared server by defining its container and writing compositions.
     * @param server the server to be created.
     * @see prepareServer
     */
    suspend fun createServer(server: Server)

    /**
     * Register a new server in the database.
     *
     * This method does not add the server to the list of available servers, this must be done before that.
     * The server object will be preserved so that there are no synchronization
     * problems due to diverging information such as: inspection.
     *
     * This method will be suspended and will only be resumed at the end of the operation.
     * @param server the server to be registered.
     */
    suspend fun registerServer(server: Server)

    /**
     * Starts a server without blocking the current thread.
     * @param server the server to be started.
     */
    suspend fun startServer(server: Server)

    /**
     * Stops a server without blocking the current thread.
     * @param server the server to be stopped.
     */
    suspend fun stopServer(server: Server)

    /**
     * Stops a server without blocking the current thread.
     * @param server the server to be stopped.
     * @param killAfter maximum execution time until force to kill the server (recommended 10 seconds).
     */
    suspend fun stopServer(server: Server, killAfter: Duration)

    /**
     * Inspect the specified server container suspending the function until the process has ended.
     * @param server the server to be inspected.
     */
    suspend fun inspectServer(server: Server)

    /**
     * Executes a command in the specified [server] container.
     * It is not possible to execute commands on containers that are not active ([ServerState.isActive]).
     *
     * @param server the server.
     * @param command the command to be executed.
     * @param options options that will go with the command.
     */
    suspend fun runServerCommand(server: Server, command: String, options: ServerCommandOptions): Flow<String>

    /**
     * Returns the state of the server's CPU, RAM and network.
     *
     * The function will suspend until the result is obtained,
     * this is not an instant operation but it is also not a slow operation,
     * it will depend on the machine on which the order is being executed.
     *
     * if you want to observe the state in real time use [receiveServerStats].
     */
    suspend fun getServerStats(server: Server): ServerStats

    /**
     * Returns a [Flow] receiving the CPU, RAM and network information
     * from the [server]. This is a continuous operation, it will suspend
     * until it is canceled or if the server  stops responding.
     *
     * It is possible to observe the initialization of the states
     * using [Flow.onStart] and finalization using [Flow.onComplete]
     *
     * You can get a single response version via [getServerStats].
     */
    suspend fun receiveServerStats(server: Server): Flow<ServerStats>

    /**
     * Returns a [Flow] receiving all logs from the [server].
     */
    suspend fun receiveServerLogs(server: Server): Flow<String>

    /**
     * Returns the [CompositionFactory] that was registered
     * for the supplied [key] or null if no factory is found for the key.
     */
    fun getCompositionFactory(key: Composition.Key): CompositionFactory?

    /**
     * Returns the [CompositionFactory] that was registered
     * for the supplied [key] or null if no factory is found for the key.
     */
    fun getCompositionFactory(key: String): CompositionFactory?

    /**
     * Registers a new compositions factory.
     * @param factory the compositions factory.
     */
    fun registerCompositionFactory(factory: CompositionFactory)

    /**
     * Unregisters a compositions factory.
     * @param factory the factory to be unregistered.
     */
    fun unregisterCompositionFactory(factory: CompositionFactory)

    fun <T : CompositionOptions> createCompositionStore(key: Composition.Key, options: T): CompositionStore<T>

}

/**
 * Returns a server with the specified [id] or `null` if the server not exists.
 * @param id the server id.
 */
fun ServerManager.getServerOrNull(id: Int): Server? {
    return runCatching {
        getServer(id)
    }.getOrNull()
}

/**
 * Returns a server with the specified [name] or `null` if the server not exists.
 * @param name the server name.
 */
fun ServerManager.getServerOrNull(name: String): Server? {
    return runCatching {
        getServer(name)
    }.getOrNull()
}


/**
 * Executes a command in the specified [server] container with the default [ServerCommandOptions].
 * It is not possible to execute commands on containers that are not active ([ServerState.isActive]).
 *
 * @param server the server.
 * @param command the command to be executed.
 * @see DefaultServerCommandOptions
 */
suspend fun ServerManager.runServerCommand(server: Server, command: String): Flow<String> {
    return runServerCommand(server, command, DefaultServerCommandOptions)
}