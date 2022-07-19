package org.katan.model.unit

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import org.katan.model.Connection

interface UnitInstance {

    val remoteAddress: Connection

}

@Serializable
private data class UnitInstanceImpl(override val remoteAddress: Connection) : UnitInstance

fun UnitInstance(remoteAddress: Connection): UnitInstance {
    return UnitInstanceImpl(remoteAddress)
}