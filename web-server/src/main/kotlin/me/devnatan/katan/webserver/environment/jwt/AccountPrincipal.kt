package me.devnatan.katan.webserver.environment.jwt

import io.ktor.auth.*
import me.devnatan.katan.api.account.Account

class AccountPrincipal(val account: Account) : Principal