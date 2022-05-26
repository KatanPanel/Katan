package org.katan.http.module.server.locations

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.TestOnly

@Serializable
@Resource("/servers")
class Servers {

    @Serializable
    @Resource("{id}")
    class Get(val parent: Servers = Servers(), val id: String)

}