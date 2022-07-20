package org.katan.http.routes.unit.locations

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/units")
internal class UnitRoutes {

    @Serializable
    @Resource("{id}")
    internal class Get(val parent: UnitRoutes = UnitRoutes(), val id: String)

}