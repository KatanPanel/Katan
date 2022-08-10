package org.katan.service.server.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/units")
internal class UnitRoutes {

    @Serializable
    @Resource("{id}")
    internal class FindById(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes(),
        val id: String
    )
    @Serializable
    @Resource("{unitId}/audit-logs")
    internal class GetUnitAuditLogs(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes(),
        val unitId: String
    )

}
