package org.katan.service.server

import org.katan.model.unit.KUnit

public interface UnitService {

    public suspend fun getUnit(id: Long): KUnit?

    public suspend fun createUnit(options: UnitCreateOptions): KUnit
}
