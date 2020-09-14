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
     * Server hostname
     */
    val address: String

    /**
     * Remote port from server address.
     */
    val port: Short

    /**
     * Accounts that have permissions on that server.
     */
    var holders: MutableList<ServerHolder>

    /**
     * Data from the Docker container linked to the server.
     */
    val container: ServerContainer

    val query: ServerQuery

}

/**
 * Server hostname
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