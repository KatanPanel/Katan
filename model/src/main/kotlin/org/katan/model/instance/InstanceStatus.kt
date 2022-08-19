package org.katan.model.instance

sealed class InstanceStatus(
    val value: String,
    val isInitialStatus: Boolean = false,
    val isRuntimeStatus: Boolean = false
) {
    object Created : InstanceStatus("created")
    object NetworkAssignmentFailed : InstanceStatus("network-assignment-failed")
    object Unavailable : InstanceStatus("unavailable")
    object Unknown : InstanceStatus("unknown")
    object ImagePullInProgress : InstanceStatus("image-pull", isInitialStatus = true)
    object ImagePullNeeded : InstanceStatus("image-pull-needed", isInitialStatus = true)
    object ImagePullFailed : InstanceStatus("image-pull-failed", isInitialStatus = true)
    object ImagePullCompleted : InstanceStatus("image-pull-completed", isInitialStatus = true)
    object Dead : InstanceStatus("dead", isRuntimeStatus = true)
    object Paused : InstanceStatus("paused", isRuntimeStatus = true)
    object Exited : InstanceStatus("exited", isRuntimeStatus = true)
    object Running : InstanceStatus("running", isRuntimeStatus = true)
    object Stopped : InstanceStatus("stopped", isRuntimeStatus = true)
    object Starting : InstanceStatus("starting", isRuntimeStatus = true)
    object Removing : InstanceStatus("removing", isRuntimeStatus = true)
    object Stopping : InstanceStatus("stopping", isRuntimeStatus = true)
    object Restarting : InstanceStatus("restarting", isRuntimeStatus = true)
}

sealed class InstanceUpdateCode(val name: String, val code: Int) {

    object Start : InstanceUpdateCode("start", 1)
    object Stop : InstanceUpdateCode("stop", 2)
    object Restart : InstanceUpdateCode("restart", 3)
    object Kill : InstanceUpdateCode("kill", 4)

    companion object {

        private val mappings: Map<Int, InstanceUpdateCode> by lazy {
            listOf(Start, Stop, Restart, Kill).associateBy { it.code }
        }

        @JvmStatic
        fun getByCode(code: Int): InstanceUpdateCode? {
            return mappings[code]
        }
    }

    override fun toString(): String {
        return "$name ($code)"
    }
}
