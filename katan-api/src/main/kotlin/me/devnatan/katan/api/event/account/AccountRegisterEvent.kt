package me.devnatan.katan.api.event.account

import me.devnatan.katan.api.security.account.Account

/**
 * Called when an already created [Account] is registered.
 * @property account the account that was registered.
 */
open class AccountRegisterEvent(override val account: Account) : AccountEvent