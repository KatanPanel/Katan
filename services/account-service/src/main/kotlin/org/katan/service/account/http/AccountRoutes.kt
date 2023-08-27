@file:Suppress("unused")

package org.katan.service.account.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/accounts")
internal class AccountRoutes {

    @Serializable
    @Resource("")
    internal class List(val parent: AccountRoutes = AccountRoutes())

    @Serializable
    @Resource("")
    internal class Register(val parent: AccountRoutes = AccountRoutes())
}
