package me.devnatan.katan.api.server

/**
 * Represents the state of a server, obtained by inspecting its container.
 * @see ServerContainerInspection
 */
enum class ServerState {

    /**
     * The internal server process is dead (aka kill).
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
     * The state of the server is unknown.
     */
    UNKNOWN

}

/**
 * Returns `true` if the server is up and running or `false` if it's [isInactive].
 */
fun ServerState.isActive(): Boolean {
    return this == ServerState.RESTARTING || this == ServerState.RUNNING
}

/**
 * Returns `true` if the server is stopped, idle, or in an unknown state or `false` otherwise.
 */
fun ServerState.isInactive(): Boolean {
    return this == ServerState.DEAD || this == ServerState.PAUSED || this == ServerState.UNKNOWN
}