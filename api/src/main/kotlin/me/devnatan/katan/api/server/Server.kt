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
    val container: ServerContainer

    val query: ServerQuery

}