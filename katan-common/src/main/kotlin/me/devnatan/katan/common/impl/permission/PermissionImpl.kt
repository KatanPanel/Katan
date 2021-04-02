package me.devnatan.katan.common.impl.permission

import me.devnatan.katan.api.security.permission.Permission
import me.devnatan.katan.api.security.permission.PermissionFlag
import me.devnatan.katan.api.security.permission.PermissionKey
import java.time.Instant

data class PermissionImpl(
    override val key: PermissionKey,
    override val value: PermissionFlag,
    override val givenAt: Instant
) : Permission {

    override var lastModifiedAt: Instant = givenAt

}