package org.katan.service.dockerNetwork

import kotlinx.serialization.Serializable
import org.katan.model.net.Connection

@Serializable
internal data class ConnectionImpl(override val host: String, override val port: Int) : Connection {

    override fun toString(): String {
        return "$host:$port"
    }
}
