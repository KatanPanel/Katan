package org.katan.service.auth.http.shared

import io.ktor.util.AttributeKey
import org.katan.model.account.Account

val AccountKey: AttributeKey<Account> = AttributeKey("account")
