package me.devnatan.katan.api.server

/**
 * @author Natan V.
 * @since 0.1.0
 */
enum class ServerState {

    /**
     * The internal server process is dead.
     */
    DEAD,

    /**
     * The server has been paused.
     */
    PAUSED,

    /**
     * Server is restarting.
     */
    RESTARTING,

    /**
     * Server is started and running.
     */
    RUNNING,

    /**
     * The state of the server is unknown,
     * existing only during pre-inspection.
     */
    UNKNOWN

}

/**
 * If the server is up and running.
 */
fun ServerState.isActive(): Boolean {
    return this == ServerState.RESTARTING || this == ServerState.RUNNING
}

/**
 * If the server is stopped, idle, or in an unknown state.
 */
fun ServerState.isInactive(): Boolean {
    return this == ServerState.DEAD || this == ServerState.PAUSED || this == ServerState.UNKNOWN
}