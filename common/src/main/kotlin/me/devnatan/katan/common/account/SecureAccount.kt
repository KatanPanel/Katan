package me.devnatan.katan.common.account

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.Permission
import me.devnatan.katan.api.security.permission.PermissionFlag
import java.time.Instant
import java.util.*

data class SecureAccount(
    override val id: UUID,
    override val username: String,
    override val registeredAt: Instant
) : Account {

    var password: String = ""
    override val permissions: MutableMap<Permission, PermissionFlag> = hashMapOf()

}