package org.katan.service.server.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.katan.service.id.validation.MustBeSnowflake

@Serializable
@Resource("/units")
internal class UnitRoutes {

    @Serializable
    @Resource("")
    internal class All(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes()
    )

    @Serializable
    @Resource("{id}")
    internal class ById(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes(),
        @field:MustBeSnowflake val id: String
    )

    @Serializable
    @Resource("{unitId}/audit-logs")
    internal class GetUnitAuditLogs(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes(),
        val unitId: String
    )
}
