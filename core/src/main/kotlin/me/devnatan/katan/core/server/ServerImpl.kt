package me.devnatan.katan.core.server

import me.devnatan.katan.api.server.*
import me.devnatan.katan.common.server.ServerCompositionsImpl

data class ServerImpl(
    override val id: Int,
    override var name: String
) : Server {

    override lateinit var container: ServerContainer
    override var query: ServerQuery = NullServerQuery()
    override var state: ServerState = ServerState.UNKNOWN
    override val holders: MutableSet<ServerHolder> = hashSetOf()
    override var compositions: ServerCompositions = ServerCompositionsImpl()

}