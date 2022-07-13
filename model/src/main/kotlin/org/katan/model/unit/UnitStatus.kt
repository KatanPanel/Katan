package org.katan.model.unit

@kotlinx.serialization.Serializable
sealed interface UnitStatus {

    val name: String

    @kotlinx.serialization.Serializable
    object Unknown : UnitStatus {
        override val name: String = "unknown"
    }

}