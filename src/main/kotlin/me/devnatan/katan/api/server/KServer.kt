@file:JvmMultifileClass
@file:JvmName("KServerInfo")
package me.devnatan.katan.api.server

interface KServer {

    /**
     * Number for unique server identification.
     */
    val id: Int

    /**
     * Server name.
     */
    val name: String

    /**
     * Remote port from server address.
     */
    val port: Short

    /**
     * Accounts that have permissions on that server.
     */
    var holders: MutableList<KServerHolder>

    /**
     * Data from the Docker container linked to the server.
     */
    val container: KServerContainer

    /**
     * Search data at the remote server address.
     */
    var query: KServerQuery

}

/**
 * IP address without server port or protocols.
 */
val KServer.address: String
    get() = throw NotImplementedError()

/**
 * Current state of the server [KServer.container].
 * @throws IllegalStateException if the server has not been inspected.
 */
val KServer.currentState: String
    get() {
        checkInspected()
        return container.inspection.state.status!!
    }

/**
 * List of server [KServer.container] states.
 * @throws IllegalStateException if the server has not been inspected.
 */
val KServer.states: Map<String, Boolean>
    get() {
        checkInspected()
        val state = container.inspection.state
        return mapOf(
            "paused"        to state.paused!!,
            "running"       to state.running!!,
            "restarting"    to state.restarting!!,
            "dead"          to state.dead!!
        )
    }

private fun KServer.checkInspected() {
    check(container.isInspected) { "Not inspected yet" }
}