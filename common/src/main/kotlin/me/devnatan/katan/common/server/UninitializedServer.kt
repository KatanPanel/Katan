package me.devnatan.katan.common.server

import me.devnatan.katan.api.server.*

class UninitializedServer(
    override var name: String,
    override var address: String,
    override var port: Int,
    override var composition: String
) : Server {

    override val id: Int
        get() = throw UninitializedPropertyAccessException()

    override lateinit var container: ServerContainer
    override lateinit var query: ServerQuery
    override lateinit var state: ServerState
    override lateinit var holders: MutableSet<ServerHolder>


}