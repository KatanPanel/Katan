package me.devnatan.katan.api.event

import me.devnatan.katan.api.account.Account
import java.time.Instant

open class AccountEvent(val account: Account) : Event

open class AccountCreateEvent(account: Account) : AccountEvent(account)

open class AccountLoginEvent(
    account: Account,
    val loggedInAt: Instant
) : AccountEvent(account)

open class AccountRegisterEvent(
    account: Account,
    val registeredAt: Instant
) : AccountEvent(account)