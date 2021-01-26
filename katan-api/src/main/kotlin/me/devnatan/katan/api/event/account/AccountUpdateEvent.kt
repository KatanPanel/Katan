package me.devnatan.katan.api.event.account

import me.devnatan.katan.api.security.account.Account

/**
 * Called when an [Account] is updated.
 * @property account the account.
 */
open class AccountUpdateEvent(override val account: Account) : AccountEvent