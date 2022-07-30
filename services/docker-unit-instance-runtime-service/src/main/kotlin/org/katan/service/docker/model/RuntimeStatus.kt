package org.katan.service.docker.model

import org.katan.model.unit.UnitInstanceStatus

internal sealed class ContainerStatus(override val name: String) : UnitInstanceStatus {

    object None : ContainerStatus("none")
    object Created : ContainerStatus("created")
    object Starting : ContainerStatus("starting")
    object Restarting : ContainerStatus("restarting")
    object Running : ContainerStatus("running")
    object Removing : ContainerStatus("removing")
    object Stopped : ContainerStatus("stopped")
    object Stopping : ContainerStatus("stopping")
    object Paused : ContainerStatus("paused")
    object Exited : ContainerStatus("exited")
    object Dead : ContainerStatus("dead")
    data class Unknown(val value: String) : ContainerStatus(value)

    override fun isRunning(): Boolean {
        return this is Starting || this is Running
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