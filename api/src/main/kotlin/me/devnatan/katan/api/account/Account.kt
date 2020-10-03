package me.devnatan.katan.api.account

import me.devnatan.katan.api.permission.PermissionHolder
import java.time.Instant
import java.util.*

interface Account : PermissionHolder {

    /**
     * Returns the unique account identification.
     */
    val id: UUID

    /**
     * Returns the account username.
     */
    val username: String

    val registeredAt: Instant

}