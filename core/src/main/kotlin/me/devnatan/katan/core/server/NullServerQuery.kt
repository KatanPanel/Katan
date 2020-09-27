package me.devnatan.katan.core.server

import me.devnatan.katan.api.server.ServerQuery
import java.time.Instant

class NullServerQuery : ServerQuery {

    override var lastQueriedAt: Instant? = null
    override var latency: Long? = null

}