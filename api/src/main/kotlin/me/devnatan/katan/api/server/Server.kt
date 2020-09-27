package me.devnatan.katan.api.server

/**
 * @author Natan V.
 * @since 0.1.0
 */
interface Server {

    /**
     * Number for unique server identification.
     */
    val id: Int

    /**
     * Server name.
     */
    var name: String

    /**
     * Server hostname
     */
    var address: String

    /**
     * Remote port from server address.
     */
    var port: Int

    /**
     * Accounts that have permissions on that server.
     */
    val holders: MutableSet<ServerHolder>

    /**
     * The container linked to the server.
     */
    var container: ServerContainer

    /**
     * Remote server address search results.
     */
    val query: ServerQuery

    /**
     * Current server state.
     */
    var state: ServerState

    /**
     *
     */
    var composition: String

}