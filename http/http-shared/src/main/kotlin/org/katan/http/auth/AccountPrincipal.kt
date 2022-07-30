package org.katan.http.auth

import io.ktor.server.auth.Principal
import org.katan.model.account.Account

data class AccountPrincipal(val account: Account) : Principal