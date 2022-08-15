package org.katan.service.auth.http.shared

import io.ktor.server.auth.Principal
import org.katan.model.account.Account

internal data class AccountPrincipal(val account: Account) : Principal
