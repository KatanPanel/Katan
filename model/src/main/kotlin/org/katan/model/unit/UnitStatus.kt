package org.katan.model.unit

import kotlinx.serialization.Serializable

@Serializable
enum class UnitStatus(val value: String) {
    Unknown("unknown"),
    Created("created"),
    MissingInstance("missing-instance")

}