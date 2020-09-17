package me.devnatan.katan.api.server

/**
 * @property id the container identification.
 */
class ServerContainer(val id: String) {

    /**
     * Results of the inspection of this container.
     */
    var inspection: ServerInspection = ServerInspection.Uninspected

}

interface ServerInspection {

    object Uninspected : ServerInspection

}

fun ServerContainer.isInspected(): Boolean {
    return synchronized(inspection) {
        inspection !is ServerInspection.Uninspected
    }
}
