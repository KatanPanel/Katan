package org.katan.service.unit.instance.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.unit.UnitInstance

@Serializable
public data class UnitInstanceResponse(
    val id: Long,
    val status: String
) {

    public constructor(instance: UnitInstance) : this(instance.id, instance.status.name)

}