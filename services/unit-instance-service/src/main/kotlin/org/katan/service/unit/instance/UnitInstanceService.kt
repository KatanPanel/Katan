package org.katan.service.unit.instance

import org.katan.model.unit.UnitInstance

public interface UnitInstanceService {

    public fun fromSpec(data: Map<String, Any>): UnitInstanceSpec

    public suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstance

}