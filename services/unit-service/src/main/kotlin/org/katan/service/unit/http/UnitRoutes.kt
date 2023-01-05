package org.katan.service.unit.http

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
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
    @Resource("{unit-id}")
    internal class ById(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes(),
        @field:MustBeSnowflake
        @SerialName("unit-id")
        val unitId: String
    )

    @Serializable
    @Resource("{unit-id}/audit-logs")
    internal class GetUnitAuditLogs(
        @Suppress("UNUSED") val parent: UnitRoutes = UnitRoutes(),
        @field:MustBeSnowflake
        @SerialName("unit-id")
        val unitId: String
    )
}
