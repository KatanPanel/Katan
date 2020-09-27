package me.devnatan.katan.webserver.impl

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.permission.Permission
import me.devnatan.katan.api.permission.PermissionFlag
import java.util.*

@Serializable
class SerializableAccount(
    @Contextual
    override val id: UUID,
    override val username: String,
    @Transient
    override var password: String = ""
) : Account {

    @Contextual
    override val permissions: MutableMap<Permission, PermissionFlag> = hashMapOf()

}