package me.devnatan.katan.api.event.account

import me.devnatan.katan.api.security.account.Account


/**
 * Called when a new [Account] is created.
 * Do not confuse creation with registration, for registration use [AccountRegisterEvent].
 * @property account the account that was created.
 */
open class AccountCreateEvent(override val account: Account) : AccountEvent