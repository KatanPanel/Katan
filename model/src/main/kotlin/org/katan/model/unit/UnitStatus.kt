package org.katan.model.unit

interface UnitStatus {

    val name: String

    object Unknown : UnitStatus {
        override val name: String = "unknown"
    }

}