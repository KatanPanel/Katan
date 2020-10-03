package me.devnatan.katan.api.server

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
     * Server compositions.
     */
    var compositions: ServerCompositions

}