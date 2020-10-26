package me.devnatan.katan.common.impl.account

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.Permission
import me.devnatan.katan.api.security.permission.PermissionFlag
import me.devnatan.katan.api.security.role.Role
import java.time.Instant
import java.util.*

data class SecureAccount(
    override val id: UUID,
    override val username: String,
    override val registeredAt: Instant
) : Account {

    var password: String = ""

    override var role: Role? = null
    override val permissions: MutableMap<Permission, PermissionFlag> = hashMapOf()

    override fun setPermission(permission: Permission, value: PermissionFlag) {
        permissions[permission] = value
    }

    @OptIn(UnstableKatanApi::class)
    override fun isPermissionAllowed(permission: Permission): Boolean {
        return when (getPermission(permission) ?: return false) {
            is PermissionFlag.Allowed -> true
            is PermissionFlag.NotAllowed -> false
            is PermissionFlag.Inherit -> role?.isPermissionAllowed(permission) ?: false
        }
    }

    @OptIn(UnstableKatanApi::class)
    override fun isPermissionNotAllowed(permission: Permission): Boolean {
        return when (getPermission(permission) ?: return true) {
            is PermissionFlag.Allowed -> false
            is PermissionFlag.NotAllowed -> true
            is PermissionFlag.Inherit -> role?.isPermissionNotAllowed(permission) ?: true
        }
    }

}