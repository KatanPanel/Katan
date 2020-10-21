package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.ServerQuery

object NonQueryableServerQuery : ServerQuery {

    override fun getLatency(): Long {
        throw UnsupportedOperationException("Non queryable")
    }

    override fun getLastQueried(): Boolean {
        throw UnsupportedOperationException("Non queryable")
    }

    override fun wasQueried(): Boolean {
        throw UnsupportedOperationException("Non queryable")
    }

}