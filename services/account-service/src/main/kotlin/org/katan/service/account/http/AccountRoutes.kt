package org.katan.service.account.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/accounts")
internal class AccountRoutes {

    @Serializable
    @Resource("")
    internal class Register(@Suppress("unused") val parent: AccountRoutes = AccountRoutes())

}