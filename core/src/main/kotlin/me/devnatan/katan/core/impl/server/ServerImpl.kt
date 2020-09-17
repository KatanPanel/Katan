package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainer
import me.devnatan.katan.api.server.ServerHolder
import me.devnatan.katan.api.server.ServerQuery

class ServerImpl(
    override val id: Int,
    override var name: String,
    override var address: String,
    override var port: Int,
    override val container: ServerContainer
) : Server {

    override val query: ServerQuery = MinecraftServerQuery(this)

    @Transient
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}