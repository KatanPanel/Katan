package me.devnatan.katan.api.account

import me.devnatan.katan.api.role.Role
import me.devnatan.katan.api.security.permission.PermissionsHolder
import java.time.Instant
import java.util.*

/**
 * Represents an account, initially only on the Katan Web Server and later on the CLI as well.
 * Accounts can be used when authentication is required to perform something.
 */
interface Account : PermissionsHolder {

    /**
     * Returns the unique account identification.
     */
    val id: UUID

    /**
     * Returns the account username.
     */
    val username: String

    /**
     * Returns when this account was registered.
     */
    val registeredAt: Instant

    /**
     * Returns the last time you authenticated with this account.
     */
    var lastLogin: Instant?

    /**
     * Returns the current [Role] of the account or `null` if it has no role.
     */
    var role: Role?

}