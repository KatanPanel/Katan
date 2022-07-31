package org.katan.http.routes.unitInstance

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/instances")
internal class UnitInstanceResource {

    @Serializable
    @Resource("{instanceId}/start")
    internal class Start(
        val parent: UnitInstanceResource = UnitInstanceResource(),
        val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/restart")
    internal class Restart(
        val parent: UnitInstanceResource = UnitInstanceResource(),
        val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/stop")
    internal class Stop(
        val parent: UnitInstanceResource = UnitInstanceResource(),
        val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/kill")
    internal class Kill(
        val parent: UnitInstanceResource = UnitInstanceResource(),
        val instanceId: String
    )
}
