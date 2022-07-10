package org.katan.service.server

import org.katan.model.unit.KUnit

public interface UnitService {

    public suspend fun list(): List<KUnit>

    public suspend fun get(id: Long): KUnit?

    public suspend fun create(options: UnitCreateOptions): KUnit

}