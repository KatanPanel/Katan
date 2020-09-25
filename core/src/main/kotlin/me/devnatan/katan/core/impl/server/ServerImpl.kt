package me.devnatan.katan.core.impl.server

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.devnatan.katan.api.server.*

@Serializable
class ServerImpl(
    override val id: Int,
    override var name: String,
    override var address: String,
    override var port: Int,
    @Contextual
    override val container: ServerContainer
) : Server {

    override val query: ServerQuery = MinecraftServerQuery(this)

    override var state: ServerState = ServerState.UNKNOWN

    @Transient
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}