package me.devnatan.katan.common.impl.account

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.Permission
import me.devnatan.katan.api.security.permission.PermissionFlag
import me.devnatan.katan.api.security.permission.PermissionKey
import me.devnatan.katan.api.security.role.Role
import me.devnatan.katan.common.impl.permission.PermissionImpl
import java.time.Instant
import java.util.*

@Deprecated("Exclusive for the Web Server module")
data class SecureAccount(
    override val id: UUID,
    override val username: String,
    override val registeredAt: Instant
) : Account {

    var password: String? = null

    override var lastLogin: Instant? = null
    override var role: Role? = null
    override val permissions: MutableList<Permission> = arrayListOf()

    override fun getPermission(key: PermissionKey): Permission? {
        return super.getPermission(key) ?: role?.getPermission(key)
    }

    override fun setPermission(key: PermissionKey, value: PermissionFlag): Permission {
        return PermissionImpl(key, value, Instant.now()).also { permission ->
            permissions.add(permission)
        }
    }

}