package org.katan.service.unit.instance

import org.katan.model.unit.UnitInstance

public interface UnitInstanceService {

    public suspend fun getInstance(id: Long): UnitInstance?

    public suspend fun deleteInstance(instance: UnitInstance)

    public fun fromSpec(data: Map<String, Any>): UnitInstanceSpec

    public suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstance

    public suspend fun startInstance(instance: UnitInstance)

    public suspend fun stopInstance(instance: UnitInstance)

    public suspend fun killInstance(instance: UnitInstance)

}