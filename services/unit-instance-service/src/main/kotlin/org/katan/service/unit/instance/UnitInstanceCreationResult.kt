package org.katan.service.unit.instance

import org.katan.model.net.Connection

public interface UnitInstanceCreationResult {

    public val address: Connection
}

internal class UnitInstanceCreationResultImpl(override val address: Connection) :
    UnitInstanceCreationResult
