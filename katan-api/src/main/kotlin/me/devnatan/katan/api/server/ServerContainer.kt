package me.devnatan.katan.api.server

/**
 * Represents a server's container.
 * @property id the container identification.
 */
abstract class ServerContainer(
    val id: String,
    val name: String
) {

    /**
     * Returns the result of the server inspection.
     */
    var inspection: ServerContainerInspection = ServerContainerInspection.NotInspected

    /**
     * Returns if the container has already been inspected.
     */
    open fun isInspected(): Boolean {
        return synchronized(inspection) {
            inspection !is ServerContainerInspection.NotInspected
        }
    }

    /**
     * Starts this [ServerContainer] and suspend this function until the container has been started.
     */
    abstract suspend fun start()

    /**
     * Stops this [ServerContainer] and suspend this function until the container has been stopped.
     */
    abstract suspend fun stop()

    override fun toString(): String {
        return "$name ($id)"
    }

}