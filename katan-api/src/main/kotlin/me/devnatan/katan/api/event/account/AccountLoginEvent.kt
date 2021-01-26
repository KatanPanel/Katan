package me.devnatan.katan.api.event.account

import me.devnatan.katan.api.security.account.Account
import java.time.Instant

/**
 * Called when an [Account] authenticates in any scope.
 * @property account the account that was authenticated.
 * @property loggedInAt when the login was performed.
 */
open class AccountLoginEvent(override val account: Account, val loggedInAt: Instant) : AccountEvent