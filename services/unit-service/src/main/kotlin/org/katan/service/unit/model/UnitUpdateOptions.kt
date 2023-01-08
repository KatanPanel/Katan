package org.katan.service.unit.model

import org.katan.model.Wrapper
import org.katan.model.unit.UnitStatus

public data class UnitUpdateOptions(
    val name: Wrapper<String>? = null,
    val actorId: Wrapper<Long>? = null,
    val instanceId: Wrapper<Long>? = null,
    val status: Wrapper<UnitStatus>? = null
)
