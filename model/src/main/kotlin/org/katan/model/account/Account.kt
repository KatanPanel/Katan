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

    val hash: String

    /**
     * The instant this account was registered.
     */
    val createdAt: Instant

    val updatedAt: Instant

    /**
     * The last instant this account was logged in.
     */
    val lastLoggedInAt: Instant?

}