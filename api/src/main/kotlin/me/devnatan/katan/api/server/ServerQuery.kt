package me.devnatan.katan.api.server

/**
 * Represents a search for data at the remote server address.
 */
interface ServerQuery {

    /**
     * Returns the latency of the last query.
     */
    fun getLatency(): Long

    /**
     * Returns the last time a query was run.
     */
    fun getLastQueried(): Boolean

    /**
     * Returns `true` if the server has been consulted at least once or` false` otherwise.
     */
    fun wasQueried(): Boolean

}