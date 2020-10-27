package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.game.GameType
import me.devnatan.katan.api.server.*

data class ServerImpl @OptIn(UnstableKatanApi::class) constructor(
    override val id: Int,
    override var name: String,
    override val gameType: GameType,
    override val compositions: ServerCompositions,
    override val host: String,
    override val port: Short
) : Server {

    companion object {

        const val HOST_ENV = "K_SERVER_HOST"
        const val PORT_ENV = "K_SERVER_PORT"

    }

    override lateinit var container: ServerContainer
    override val metadata: MutableMap<String, Any> = hashMapOf()
    override var state: ServerState = ServerState.UNKNOWN
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}