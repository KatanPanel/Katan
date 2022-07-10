package org.katan.service.unit.instance

public interface UnitInstanceService {

    public suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstanceCreationResult

}