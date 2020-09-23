package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerQuery
import java.time.Instant

class MinecraftServerQuery(val server: Server) : ServerQuery {

    override var latency: Long? = null
    override var lastQueriedAt: Instant? = null

}