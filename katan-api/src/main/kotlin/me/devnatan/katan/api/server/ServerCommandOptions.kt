package me.devnatan.katan.api.server

/**
 * Options for running a command on a [Server] container.
 * @see [ServerManager.runServerCommand]
 */
interface ServerCommandOptions {

    /**
     * The working directory inside the [Server] container.
     */
    var wkdir: String?

    /**
     * Set command environment variables.
     */
    var env: Map<String, String>

    /**
     * Username or UID.
     */
    var user: String?

    /**
     * Allocate a pseudo-TTY.
     */
    var tty: Boolean

    /**
     * Give extended privileges to the command?
     */
    var privilegied: Boolean

}

/**
 * Standard command execution options when none are specified.
 */
object DefaultServerCommandOptions : ServerCommandOptions {

    override var wkdir: String? = null
    override var env: Map<String, String> = emptyMap()
    override var tty: Boolean = false
    override var user: String? = null
    override var privilegied: Boolean = false

}

private data class ServerCommandOptionsImpl(
    override var wkdir: String?,
    override var env: Map<String, String>,
    override var tty: Boolean,
    override var user: String?,
    override var privilegied: Boolean
) : ServerCommandOptions

/**
 * Returns [ServerCommandOptions] with the specified [wkdir] and [env].
 */
fun ServerCommandOptions(
    wkdir: String? = null,
    env: Map<String, String> = emptyMap(),
    tty: Boolean = false,
    user: String? = null,
    privilegied: Boolean = false,
): ServerCommandOptions {
    return ServerCommandOptionsImpl(wkdir, env, tty, user, privilegied)
}