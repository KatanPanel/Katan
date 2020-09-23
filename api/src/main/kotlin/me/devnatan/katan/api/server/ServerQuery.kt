package me.devnatan.katan.api.server

import java.time.Instant

/**
 * @author Natan V.
 * @since 0.1.0
 */
interface ServerQuery {

    /**
     * Last query latency.
     * Returns null if it has never been performed.
     */
    var latency: Long?

    /**
     * Last time who ran a query.
     * Returns null if it has never been performed.
     */
    var lastQueriedAt: Instant?

}