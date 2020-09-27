package me.devnatan.katan.core.server

import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.permission.Permission
import me.devnatan.katan.api.permission.PermissionFlag
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder
import java.util.*

class ServerHolderImpl(
    override val account: Account,
    override val server: Server
) : ServerHolder {

    override lateinit var permissions: EnumMap<Permission, PermissionFlag>

}