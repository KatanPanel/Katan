package org.katan.model.unit

sealed class UnitInstanceStatus(val name: String) {

    object None : UnitInstanceStatus("none")
    object Dead : UnitInstanceStatus("dead")
    object Paused : UnitInstanceStatus("paused")
    object Exited : UnitInstanceStatus("exited")
    object Created : UnitInstanceStatus("created")
    object Running : UnitInstanceStatus("running")
    object Stopped : UnitInstanceStatus("stopped")
    object Starting : UnitInstanceStatus("starting")
    object Removing : UnitInstanceStatus("removing")
    object Stopping : UnitInstanceStatus("stopping")
    object Restarting : UnitInstanceStatus("restarting")
}

sealed class UnitInstanceUpdateStatusCode(val name: String, val code: Int) {

    object Start : UnitInstanceUpdateStatusCode("start", 1)
    object Stop : UnitInstanceUpdateStatusCode("stop", 2)
    object Restart : UnitInstanceUpdateStatusCode("restart", 3)
    object Kill : UnitInstanceUpdateStatusCode("kill", 4)

    companion object {

        private val mappings: Map<Int, UnitInstanceUpdateStatusCode> by lazy {
            listOf(Start, Stop, Restart, Kill).associateBy { it.code }
        }

        @JvmStatic
        fun getByCode(code: Int): UnitInstanceUpdateStatusCode? {
            return mappings[code]
        }
    }

    override fun toString(): String {
        return "$name ($code)"
    }
}
