package org.katan.service.unit.model

import org.katan.model.Snowflake
import org.katan.model.Wrapper
import org.katan.model.unit.UnitStatus

public data class UnitUpdateOptions(
    val name: Wrapper<String>? = null,
    val actorId: Wrapper<Snowflake>? = null,
    val instanceId: Wrapper<Snowflake>? = null,
    val status: Wrapper<UnitStatus>? = null
)
