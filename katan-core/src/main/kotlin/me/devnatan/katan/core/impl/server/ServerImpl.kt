package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.composition.Compositions
import me.devnatan.katan.api.server.*

data class ServerImpl(
    override val id: Int,
    override var name: String,
    override val game: ServerGame,
    override val compositions: Compositions,
    override val host: String,
    override val port: Short
) : Server {

    override lateinit var container: ServerContainer
    override var state: ServerState = ServerState.UNKNOWN
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}