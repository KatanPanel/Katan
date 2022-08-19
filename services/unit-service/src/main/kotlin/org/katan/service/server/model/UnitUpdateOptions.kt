package org.katan.service.server.model

import org.katan.model.internal.Wrapper
import org.katan.model.unit.UnitStatus

public data class UnitUpdateOptions(
    val name: Wrapper<String>? = null,
    val actorId: Wrapper<Long>? = null,
    val instanceId: Wrapper<Long>? = null,
    val status: Wrapper<UnitStatus>? = null
)
