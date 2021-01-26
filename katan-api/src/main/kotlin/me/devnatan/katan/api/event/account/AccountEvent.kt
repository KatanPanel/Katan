package me.devnatan.katan.api.event.account

import me.devnatan.katan.api.event.Event
import me.devnatan.katan.api.security.account.Account

/**
 * Represents an event that contains an [Account] in its context.
 */
interface AccountEvent : Event {

    /**
     * Returns the account involved with the event.
     */
    val account: Account?

}