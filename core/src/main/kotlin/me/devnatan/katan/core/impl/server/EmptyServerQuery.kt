package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.ServerQuery
import java.time.Instant

object EmptyServerQuery : ServerQuery {

    override fun getLatency(): Long {
        return -1L
    }

    override fun getLastQueried(): Instant? {
        return null
    }

    override fun wasQueried(): Boolean {
        return false
    }

    override fun data(): Any? {
        return null
    }

}