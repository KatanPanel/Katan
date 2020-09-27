package me.devnatan.katan.webserver.impl

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
    override lateinit var query: ServerQuery
    override lateinit var state: ServerState

    @Transient
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}