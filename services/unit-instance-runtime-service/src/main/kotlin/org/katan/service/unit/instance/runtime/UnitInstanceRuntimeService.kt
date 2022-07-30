package org.katan.service.unit.instance.runtime

public interface UnitInstanceRuntimeService {

    public suspend fun createRuntime(options: UnitInstanceRuntimeOptions)

    public suspend fun removeRuntime(id: String)

    public suspend fun stopRuntime(id: String)

}