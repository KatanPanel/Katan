package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.Permission
import me.devnatan.katan.api.security.permission.PermissionFlag
import me.devnatan.katan.api.security.permission.PermissionKey
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder
import me.devnatan.katan.common.impl.permission.PermissionImpl
import java.time.Instant

data class ServerHolderImpl(override val account: Account, override val server: Server) : ServerHolder {

    override val permissions: MutableList<Permission> = arrayListOf()

    override fun setPermission(key: PermissionKey, value: PermissionFlag): Permission {
        return PermissionImpl(key, value, Instant.now()).also { permission ->
            permissions.add(permission)
        }
    }

}