package org.katan.model.unit

interface UnitInstanceStatus {

    val name: String

    fun isRunning(): Boolean

}