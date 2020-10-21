package me.devnatan.katan.api.security.account

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.security.permission.PermissionsHolder
import me.devnatan.katan.api.security.role.Role
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

    @UnstableKatanApi
    var role: Role?

}