package me.devnatan.katan.api.manager

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import java.time.Duration

interface ServerManager {

    /**
     * Returns a copy of all registered servers.
     */
    fun getServerList(): Collection<Server>

    /**
     * Returns a server with the specified [id].
     *
     * @param id the server id
     * @throws NoSuchElementException if the server in question does not exist.
     * @return the server
     */
    fun getServer(id: Int): Server

    /**
     * Returns a server with the specified [name].
     *
     * @param name the server name
     * @throws NoSuchElementException if the server in question does not exist.
     * @return the server
     */
    fun getServer(name: String): Server

    /**
     * Adds a server to the list of available servers.
     *
     * @param server the server to be added
     * @return whether the server was successfully added
     */
    fun addServer(server: Server): Boolean

    /**
     * Checks if there is a server registered with the specified [id].
     * @param id the server id
     */
    fun existsServer(id: Int): Boolean

    /**
     * Checks if there is a server registered with the specified [name].
     * @param name the server name
     */
    fun existsServer(name: String): Boolean

    /**
     * Initializes the attributes of a server (creates the container for example).
     * @param server the server to be created
     */
    suspend fun createServer(server: Server): Server

    /**
     * Register a new server in the database.
     *
     * This method does not add the server to the list of available servers, this must be done before that.
     * The server object will be preserved so that there are no synchronization
     * problems due to diverging information such as: inspection.
     *
     * This method will be suspended and will only be resumed at the end of the operation.
     * @param server the server to be registered
     */
    suspend fun registerServer(server: Server)

    /**
     * Starts a server without blocking the current thread.
     *
     * It is possible to know if the server was started successfully
     * through [Job.invokeOnCompletion], or using [Job.join] with try-with-resources directly.
     * @param server the server to be started
     */
    suspend fun startServer(server: Server)

    /**
     * Stops a server without blocking the current thread.
     *
     * It is possible to know if the server was stopped successfully
     * through [Job.invokeOnCompletion], or using [Job.join] with try-with-resources directly.
     * @param server the server to be stopped
     * @param killAfter maximum execution time until force to kill the server (recommended default: 10 seconds)
     */
    suspend fun stopServer(server: Server, killAfter: Duration)

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
     */
    suspend fun inspectServer(server: Server)

    /**
     * Lazily executes a [command] in the specified [server] container.
     * Calling this function will not execute the command immediately,
     * it will only prepare you for a post initialization, use [Deferred.start] to execute the command.
     *
     * Example of usage:
     * ```
     * runServer(server, command, options).onCompletion {
     *     println("Container detached")
     * }.onEach { value ->
     *     println("Output: $value")
     * }
     * ```
     * @param server the server
     * @param command the command to be executed
     */
    fun runServer(server: Server, command: String): Flow<String>

    fun getRegisteredCompositionFactories(): Collection<ServerCompositionFactory>

    fun getCompositionFactoryFor(key: ServerComposition.Key<*>): ServerCompositionFactory?

    fun getCompositionFactoryApplicableFor(name: String): ServerCompositionFactory?

    fun registerCompositionFactory(factory: ServerCompositionFactory)

    fun unregisterCompositionFactory(factory: ServerCompositionFactory)

}