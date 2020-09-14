package me.devnatan.katan.api.server

import java.time.Instant

interface ServerQuery {

    var latency: Long

    var lastQueriedAt: Instant

}