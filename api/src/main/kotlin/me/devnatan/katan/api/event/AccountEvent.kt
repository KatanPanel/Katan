package me.devnatan.katan.api.event

import me.devnatan.katan.api.security.account.Account
import java.time.Instant

/**
 * Represents an event that contains an [Account] in its context.
 */
interface AccountEvent : Event {

    /**
     * Returns the account involved with the event.
     */
    val account: Account?

}

/**
 * Called when a new account is created.
 * Do not confuse creation with registration, for registration use [AccountRegisterEvent].
 * @property account the account that was created.
 */
open class AccountCreateEvent(override val account: Account) : AccountEvent

/**
 * Called when an account authenticates in any scope.
 * @property account the account that was authenticated.
 * @property loggedInAt when the login was performed.
 */
open class AccountLoginEvent(override val account: Account, val loggedInAt: Instant) : AccountEvent

/**
 * Called when an already created account is registered.
 * @property account the account that was registered.
 * @property registeredAt when the account was registered.
 */
open class AccountRegisterEvent(override val account: Account, val registeredAt: Instant) : AccountEvent