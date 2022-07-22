package org.katan.model.unit

interface UnitInstance {

    val id: Long

    val image: String

    val status: UnitInstanceStatus

    val container: String

}

sealed class UnitInstanceStatus(val name: String) {

    object None : UnitInstanceStatus("none")
    object Created : UnitInstanceStatus("created")
    object Starting : UnitInstanceStatus("starting")
    object Restarting : UnitInstanceStatus("restarting")
    object Running : UnitInstanceStatus("running")
    object Removing : UnitInstanceStatus("removing")
    object Stopped : UnitInstanceStatus("stopped")
    object Stopping : UnitInstanceStatus("stopping")
    object Paused : UnitInstanceStatus("paused")
    object Exited : UnitInstanceStatus("exited")
    object Dead : UnitInstanceStatus("dead")
    data class Unknown(val value: String) : UnitInstanceStatus(value)

    fun canBeStopped(): Boolean {
        return this is Running || this is Paused || this is Restarting
    }

    companion object {

        private val mappings: List<UnitInstanceStatus> by lazy {
            listOf(
                None,
                Created,
                Starting,
                Restarting,
                Running,
                Removing,
                Stopped,
                Stopping,
                Paused,
                Exited,
                Dead
            )
        }

        @JvmStatic
        fun getByName(name: String): UnitInstanceStatus {
            return mappings.find { it.name.equals(name, ignoreCase = false) }
                ?: Unknown(name)
        }

    }

}

private data class UnitInstanceImpl(
    override val id: Long,
    override val image: String,
    override val status: UnitInstanceStatus,
    override val container: String
) : UnitInstance

fun UnitInstance(id: Long, dockerImage: String, status: UnitInstanceStatus, container: String): UnitInstance {
    return UnitInstanceImpl(id, dockerImage, status, container)
}