package org.katan.http.routes.server.locations

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/servers")
internal class Servers {

    @Serializable
    @Resource("{id}")
    internal class Get(val parent: Servers = Servers(), val id: String)

}