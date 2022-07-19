package org.katan.model.unit

@kotlinx.serialization.Serializable
sealed interface UnitStatus {

    val name: String

    object Unknown : UnitStatus {
        override val name: String = "unknown"
    }

    object Created : UnitStatus {
        override val name: String = "created"
    }

    object MissingInstance : UnitStatus {
        override val name: String = "missing-instance"
    }

}