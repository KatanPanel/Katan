package me.devnatan.katan.core.server

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.Permission
import me.devnatan.katan.api.security.permission.PermissionFlag
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder

data class ServerHolderImpl(override val account: Account, override val server: Server) : ServerHolder {

    override val permissions: Map<Permission, PermissionFlag> = hashMapOf()

}