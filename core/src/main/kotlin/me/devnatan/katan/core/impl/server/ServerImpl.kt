package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.*

class ServerImpl(
    override val id: Int,
    override var name: String,
    override var address: String,
    override var port: Int,
    override val container: ServerContainer
) : Server {

    override val query: ServerQuery = MinecraftServerQuery(this)

    override var state: ServerState = ServerState.UNKNOWN

    @Transient
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}