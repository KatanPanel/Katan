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
