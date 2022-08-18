package org.katan.service.server.model

import org.katan.model.unit.UnitStatus

public data class UnitUpdateOptions(
    val name: String? = null,
    val actorId: Long? = null,
    val instanceId: Long? = null,
    val status: UnitStatus? = null
)
