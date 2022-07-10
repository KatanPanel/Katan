package org.katan.model.account

import kotlinx.datetime.Instant

interface Account {

    /**
     * The unique ID of this account.
     */
    val id: Long

    /**
     * User-provided username of this account.
     */
    val username: String

    /**
     * The instant this account was registered.
     */
    val registeredAt: Instant

    /**
     * The last instant this account was logged in.
     */
    val lastLoggedInAt: Instant?

    /**
     * If this account is deactivated for login.
     */
    val deactivated: Boolean

}