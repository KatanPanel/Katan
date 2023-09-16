package org.katan.model.instance

import kotlinx.serialization.Serializable

@Serializable
sealed class InstanceStatus(
    val value: String,
    val isInitialStatus: Boolean = false,
    val isRuntimeStatus: Boolean = false,
) {
    data object Created : InstanceStatus("created")
    data object NetworkAssignmentFailed : InstanceStatus("network-assignment-failed")
    data object Unavailable : InstanceStatus("unavailable")
    data object Unknown : InstanceStatus("unknown")
    data object ImagePullInProgress : InstanceStatus("image-pull", isInitialStatus = true)
    data object ImagePullNeeded : InstanceStatus("image-pull-needed", isInitialStatus = true)
    data object ImagePullFailed : InstanceStatus("image-pull-failed", isInitialStatus = true)
    data object ImagePullCompleted : InstanceStatus("image-pull-completed", isInitialStatus = true)
    data object Dead : InstanceStatus("dead", isRuntimeStatus = true)
    data object Paused : InstanceStatus("paused", isRuntimeStatus = true)
    data object Exited : InstanceStatus("exited", isRuntimeStatus = true)
    data object Running : InstanceStatus("running", isRuntimeStatus = true)
    data object Stopped : InstanceStatus("stopped", isRuntimeStatus = true)
    data object Starting : InstanceStatus("starting", isRuntimeStatus = true)
    data object Removing : InstanceStatus("removing", isRuntimeStatus = true)
    data object Stopping : InstanceStatus("stopping", isRuntimeStatus = true)
    data object Restarting : InstanceStatus("restarting", isRuntimeStatus = true)
}

@Serializable
@Suppress("detekt.MagicNumber")
sealed class InstanceUpdateCode(val name: String, val code: Int) {

    object Start : InstanceUpdateCode(name = "start", code = 1)
    object Stop : InstanceUpdateCode(name = "stop", code = 2)
    object Restart : InstanceUpdateCode(name = "restart", code = 3)
    object Kill : InstanceUpdateCode(name = "kill", code = 4)

    companion object {

        private val mappings: Map<Int, InstanceUpdateCode> by lazy {
            listOf(Start, Stop, Restart, Kill).associateBy { it.code }
        }

        @JvmStatic
        fun getByCode(code: Int): InstanceUpdateCode? = mappings[code]
    }

    override fun toString(): String {
        return "$name ($code)"
    }
}
