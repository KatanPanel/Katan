package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.*

data class ServerImpl @OptIn(UnstableKatanApi::class) constructor(
    override val id: Int,
    override var name: String,
    override val target: ServerTarget,
    override val compositions: ServerCompositions
) : Server {

    override lateinit var container: ServerContainer
    override var query: ServerQuery = NonQueryableServerQuery
    override var state: ServerState = ServerState.UNKNOWN
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}