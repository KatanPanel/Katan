package me.devnatan.katan.webserver.serializable

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerContainer
import me.devnatan.katan.api.server.get

@OptIn(UnstableKatanApi::class)
class SerializableServer(delegate: Server) {

    class Container(delegate: ServerContainer) {

        val id = delegate.id
        val isInspected = delegate.isInspected()

    }

    val id = delegate.id
    val name = delegate.name
    val state = delegate.state
    val compositions = delegate.compositions.map { it.factory.get(it.key) }
    val container = Container(delegate.container)

}

fun Server.serializable() = SerializableServer(this)