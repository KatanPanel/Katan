package org.katan.service.unit.instance.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/instances")
internal class UnitInstanceRoutes {

    @Serializable
    @Resource("{instanceId}/start")
    internal class Start(
        @Suppress("UNUSED") val parent: UnitInstanceRoutes = UnitInstanceRoutes(),
        val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/restart")
    internal class Restart(
        @Suppress("UNUSED") val parent: UnitInstanceRoutes = UnitInstanceRoutes(),
        val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/stop")
    internal class Stop(
        @Suppress("UNUSED") val parent: UnitInstanceRoutes = UnitInstanceRoutes(),
        val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/kill")
    internal class Kill(
        @Suppress("UNUSED") val parent: UnitInstanceRoutes = UnitInstanceRoutes(),
        val instanceId: String
    )
}
