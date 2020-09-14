package me.devnatan.katan.api.server

import com.github.dockerjava.api.command.InspectContainerResponse

/**
 * @property id the container identification.
 */
class ServerContainer(
    val id: String
) {

    /**
     * Results of the inspection of this container, defined late.
     */
    lateinit var inspection: InspectContainerResponse

    /**
     * If the container has already been inspected.
     */
    var isInspected: Boolean = false

}