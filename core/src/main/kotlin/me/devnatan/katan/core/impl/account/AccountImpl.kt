package me.devnatan.katan.core.impl.account

import com.google.common.collect.Maps
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.permission.Permission
import me.devnatan.katan.api.permission.PermissionFlag
import java.util.*

@Serializable
data class AccountImpl(
    @Contextual
    override val id: UUID,
    override val username: String,
    @Transient
    override var password: String = ""
) : Account {

    @Contextual
    override var permissions: EnumMap<Permission, PermissionFlag> = Maps.newEnumMap(Permission::class.java)

}