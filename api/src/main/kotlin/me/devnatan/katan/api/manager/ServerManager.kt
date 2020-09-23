package me.devnatan.katan.api.manager

import kotlinx.coroutines.Job
import me.devnatan.katan.api.server.Server
import java.time.Duration

/**
 * @author Natan V.
 * @since 0.1.0
 */
interface ServerManager {

    /**
     * Returns a server with the specified [id].
     *
     * @param id the server id
     * @throws NoSuchElementException if the server in question does not exist.
     * @return the server
     */
    fun getServer(id: Int): Server

    /**
     * Adds a server to the list of available servers.
     *
     * @param server the server to be added
     * @return whether the server was successfully added
     */
    fun addServer(server: Server): Boolean

    /**
     * Register a new server in the database.
     *
     * This method does not add the server to the list of available servers, this must be done before that.
     * The server object will be preserved so that there are no synchronization
     * problems due to diverging information such as: inspection.
     *
     * This method will be suspended and will only be resumed at the end of the operation.
     *
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
     * Run a query in the specified server.
     * @param server the server to be queried
     */
    suspend fun queryServer(server: Server)


}