package me.devnatan.katan.core.server

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.devnatan.katan.api.server.*

@Serializable
class SerializableServer(
    override val id: Int,
    override var name: String,
    override var address: String,
    override var port: Int,
    override var composition: String
) : Server {

    @Contextual
    override lateinit var container: ServerContainer

    override var query: ServerQuery = MinecraftServerQuery(this)

    override var state: ServerState = ServerState.UNKNOWN

    @Transient
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}