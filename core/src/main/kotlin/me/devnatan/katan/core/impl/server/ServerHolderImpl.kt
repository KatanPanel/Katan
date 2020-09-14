package me.devnatan.katan.core.impl.server

import com.google.common.collect.Maps
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.permission.Permission
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder
import java.util.*

class ServerHolderImpl(
    override val account: Account,
    override val server: Server
) : ServerHolder {

    override var permissions: EnumMap<Permission, Int> = Maps.newEnumMap(Permission::class.java)

}