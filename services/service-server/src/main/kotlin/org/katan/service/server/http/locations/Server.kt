package org.katan.service.server.http.locations

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.TestOnly

@Serializable
@Resource("/servers")
internal class Servers {

    @Serializable
    @Resource("{id}")
    internal class Get(val parent: Servers = Servers(), val id: String)

}