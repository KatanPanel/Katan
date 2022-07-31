package org.katan.http.routes.unit

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/units")
internal class UnitResource {

    @Serializable
    @Resource("{id}")
    internal class ById(val parent: UnitResource = UnitResource(), val id: String)
}
