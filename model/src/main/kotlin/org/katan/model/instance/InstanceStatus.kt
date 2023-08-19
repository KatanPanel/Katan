package org.katan.model.instance

public sealed class InstanceStatus(
    public val value: String,
    public val isInitialStatus: Boolean = false,
    public val isRuntimeStatus: Boolean = false
) {
    public data object Created : InstanceStatus("created")
    public data object NetworkAssignmentFailed : InstanceStatus("network-assignment-failed")
    public data object Unavailable : InstanceStatus("unavailable")
    public data object Unknown : InstanceStatus("unknown")
    public data object ImagePullInProgress : InstanceStatus("image-pull", isInitialStatus = true)
    public data object ImagePullNeeded : InstanceStatus("image-pull-needed", isInitialStatus = true)
    public data object ImagePullFailed : InstanceStatus("image-pull-failed", isInitialStatus = true)
    public data object ImagePullCompleted : InstanceStatus("image-pull-completed", isInitialStatus = true)
    public data object Dead : InstanceStatus("dead", isRuntimeStatus = true)
    public data object Paused : InstanceStatus("paused", isRuntimeStatus = true)
    public data object Exited : InstanceStatus("exited", isRuntimeStatus = true)
    public data object Running : InstanceStatus("running", isRuntimeStatus = true)
    public data object Stopped : InstanceStatus("stopped", isRuntimeStatus = true)
    public data object Starting : InstanceStatus("starting", isRuntimeStatus = true)
    public data object Removing : InstanceStatus("removing", isRuntimeStatus = true)
    public data object Stopping : InstanceStatus("stopping", isRuntimeStatus = true)
    public data object Restarting : InstanceStatus("restarting", isRuntimeStatus = true)
}

@Suppress("detekt.MagicNumber")
public sealed class InstanceUpdateCode(public val name: String, public val code: Int) {

    public object Start : InstanceUpdateCode(name = "start", code = 1)
    public object Stop : InstanceUpdateCode(name = "stop", code = 2)
    public object Restart : InstanceUpdateCode(name = "restart", code = 3)
    public object Kill : InstanceUpdateCode(name = "kill", code = 4)

    public companion object {

        private val mappings: Map<Int, InstanceUpdateCode> by lazy {
            listOf(Start, Stop, Restart, Kill).associateBy { it.code }
        }

        @JvmStatic
        public fun getByCode(code: Int): InstanceUpdateCode? = mappings[code]
    }

    override fun toString(): String {
        return "$name ($code)"
    }
}
