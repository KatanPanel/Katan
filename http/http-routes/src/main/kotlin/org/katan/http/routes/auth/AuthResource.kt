package org.katan.http.routes.auth

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/auth")
internal class AuthResource {

    @Serializable
    @Resource("login")
    internal class Login(val parent: AuthResource = AuthResource())

    @Serializable
    @Resource("{token}")
    internal class Verify(
        val parent: AuthResource = AuthResource(),
        val token: String
    )

}