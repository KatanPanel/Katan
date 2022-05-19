package org.katan.http.module.server.locations

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/servers")
object Servers {

    @Serializable
    @Resource("{id}")
    class Get(val id: String)

    @Serializable
    @Resource("")
    class Create

}