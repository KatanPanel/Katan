package org.katan.http.locations

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
    object Create

}