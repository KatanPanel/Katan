package me.devnatan.katan.core.impl.account

import com.google.common.collect.Maps
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.permission.Permission
import java.util.*

data class AccountImpl(
    override val id: UUID,
    override val username: String,
    override var password: String
) : Account {

    override var permissions: EnumMap<Permission, Int> = Maps.newEnumMap(Permission::class.java)

}