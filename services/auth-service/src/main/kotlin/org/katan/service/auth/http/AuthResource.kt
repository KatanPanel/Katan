package org.katan.service.auth.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/auth")
internal class AuthResource {

    @Serializable
    @Resource("login")
    internal class Login(@Suppress("unused") val parent: AuthResource = AuthResource())

    @Serializable
    @Resource("")
    internal class Verify(
        @Suppress("unused") val parent: AuthResource = AuthResource(),
        val token: String? = null
    )

}