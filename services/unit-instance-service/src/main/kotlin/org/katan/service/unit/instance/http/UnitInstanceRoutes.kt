package org.katan.service.unit.instance.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.katan.service.id.validation.MustBeSnowflake

@Serializable
@Resource("/instances")
internal class UnitInstanceRoutes {

    @Serializable
    @Resource("{instanceId}")
    internal class ById(
        @Suppress("UNUSED") val parent: UnitInstanceRoutes = UnitInstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/status")
    internal class UpdateStatus(
        @Suppress("UNUSED") val parent: UnitInstanceRoutes = UnitInstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String
    )
}
