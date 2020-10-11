package me.devnatan.katan.common.server

import me.devnatan.katan.api.server.*

class UninitializedServer(override var name: String, override val target: String) : Server {

    override val id: Int
        get() = throw UninitializedPropertyAccessException()

    override var compositions: ServerCompositions = ServerCompositionsImpl()

    override lateinit var container: ServerContainer
    override lateinit var query: ServerQuery
    override lateinit var state: ServerState
    override lateinit var holders: MutableSet<ServerHolder>


}