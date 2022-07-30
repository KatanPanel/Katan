package org.katan.service.unit.instance.runtime

import org.katan.model.unit.UnitInstanceStatus

public interface UnitInstanceRuntimeService {

    public suspend fun createRuntime(options: UnitInstanceRuntimeOptions)

    public suspend fun removeRuntime(id: String)

    public suspend fun stopRuntime(id: String, newStatus: UnitInstanceStatus)

}