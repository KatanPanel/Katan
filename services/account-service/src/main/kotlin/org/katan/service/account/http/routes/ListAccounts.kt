package org.katan.service.account.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.response.respond
import org.katan.service.account.AccountService
import org.katan.service.account.http.AccountRoutes
import org.katan.service.account.http.dto.AccountResponse
import org.koin.ktor.ext.inject

internal fun Route.listAccounts() {
    val accountService by inject<AccountService>()

    get<AccountRoutes.List> {
        val accounts = accountService.listAccounts()
        respond(accounts.map(::AccountResponse))
    }
}
