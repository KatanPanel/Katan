package me.devnatan.katan.webserver.serializable

import com.fasterxml.jackson.annotation.JsonIgnore
import me.devnatan.katan.api.server.Server

class SerializableServer(delegate: Server) : Server by delegate {

    @JsonIgnore
    override val holders = delegate.holders

}