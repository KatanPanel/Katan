package org.katan.service.server

import org.katan.model.unit.KUnit

public fun interface UnitFactory {

    public suspend fun create(options: UnitCreateOptions): KUnit

}