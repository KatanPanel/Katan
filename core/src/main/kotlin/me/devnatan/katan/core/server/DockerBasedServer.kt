package me.devnatan.katan.core.server

import me.devnatan.katan.api.server.*

class DockerBasedServer(
    override val id: Int,
    override var name: String,
    override var address: String,
    override var port: Int,
    override var composition: String
) : Server {

    override lateinit var container: ServerContainer
    override var query: ServerQuery = MinecraftServerQuery(this)
    override var state: ServerState = ServerState.UNKNOWN
    override val holders: MutableSet<ServerHolder> = hashSetOf()

}