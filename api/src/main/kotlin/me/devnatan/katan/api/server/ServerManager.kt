package me.devnatan.katan.api.server

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import java.time.Duration

/**
 * Responsible for the generation, handling and handling of absolutely
 * everything related to servers, from creation to query.
 */
interface ServerManager {

    /**
     * Returns a copy of all registered servers.
     */
    fun getServerList(): Collection<Server>

    /**
     * Returns a server with the specified id.
     * @param id the server id.
     * @throws NoSuchElementException if the server not exists.
     */
    fun getServer(id: Int): Server

    /**
     * Returns a server with the specified name.
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
     *
     * It is possible to know if the server was started successfully via
     * [Job.invokeOnCompletion], or using [Job.join] with try-with-resources directly.
     * @param server the server to be started.
     */
    suspend fun startServer(server: Server)

    /**
     * Stops a server without blocking the current thread.
     *
     * It is possible to know if the server was stopped successfully via
     * [Job.invokeOnCompletion], or using [Job.join] with try-with-resources directly.
     * @param server the server to be stopped.
     * @param killAfter maximum execution time until force to kill the server (recommended & default: 10 seconds).
     */
    suspend fun stopServer(server: Server, killAfter: Duration)

    /**
     * Inspect the specified server container suspending the function until the process has ended.
     * @param server the server to be inspected.
     */
    suspend fun inspectServer(server: Server)

    /**
     * Executes a command in the specified server's container suspending the function until the process has ended.
     * Flow will be canceled automatically when you stop receiving responses from the request.
     *
     * Example of usage:
     * ```
     * runServerCommand(server, command).onStart {
     *     // attached
     * }.onCompletion {
     *     // detached
     * }.collect { response ->
     *     // output
     * }
     * ```
     * @param server the server.
     * @param command the command to be executed.
     */
    suspend fun runServerCommand(server: Server, command: String): Flow<String>

    /**
     * Returns the [ServerCompositionFactory] that was registered
     * for the supplied [key] or null if no factory is found for the key.
     */
    fun getCompositionFactory(key: ServerComposition.Key<*>): ServerCompositionFactory?

    /**
     * Returns the [ServerCompositionFactory] that was registered
     * for the supplied [name] or null if no factory is found for the key.
     */
    fun getCompositionFactory(name: String): ServerCompositionFactory?

    /**
     * Registers a new compositions factory.
     * @param factory the compositions factory.
     */
    fun registerCompositionFactory(factory: ServerCompositionFactory)

    /**
     * Unregisters a compositions factory.
     * @param factory the factory to be unregistered.
     */
    fun unregisterCompositionFactory(factory: ServerCompositionFactory)

}