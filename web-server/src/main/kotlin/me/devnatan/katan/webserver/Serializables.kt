@file:OptIn(UnstableKatanApi::class)

package me.devnatan.katan.webserver

import io.ktor.http.*
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.plugin.Plugin
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.*
import me.devnatan.katan.api.security.role.Role
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerHolder
import me.devnatan.katan.api.server.get

private fun mapPermissions(entity: PermissionsHolder, permissionManager: PermissionManager): List<Map<String, Any?>> {
    return permissionManager.getRegisteredPermissionKeys().filter { key ->
        when (entity) {
            is Account -> key.isTypeOf(PermissionKeyType.ACCOUNT)
            is Role -> key.isTypeOf(PermissionKeyType.ROLE)
            is ServerHolder -> key.isTypeOf(PermissionKeyType.SERVER_HOLDER)
            else -> true
        }
    }.sortedBy { key -> key.code }.map { key ->
        entity.getPermission(key)?.let { permission ->
            mapOf(
                "key" to key,
                "value" to permission.value.code,
                "given_at" to permission.givenAt.toHttpDateString(),
                "last_modified" to permission.lastModified.toHttpDateString()
            )
        } ?: mapOf("key" to key, "value" to PermissionFlag.NOT_ALLOWED.code)
    }
}

@OptIn(UnstableKatanApi::class)
fun Server.serialize(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "state" to state,
    "game" to mapOf(
        "name" to game.type.name,
        "version" to game.version
    ),
    "host" to host,
    "port" to port,
    "compositions" to compositions.map { it.factory[it.key] },
    "container" to mapOf(
        "id" to container.id,
        "is_inspected" to container.isInspected(),
        "inspection" to container.inspection
    )
)

fun Account.serialize(
    permissionManager: PermissionManager
): Map<String, Any?> = mapOf(
    "id" to id,
    "username" to username,
    "registered_at" to registeredAt.toHttpDateString(),
    "last_login" to lastLogin?.toHttpDateString(),
    "role" to role?.let { role ->
        mapOf(
            "id" to role.id,
            "name" to role.name,
            "created_at" to role.createdAt.toHttpDateString(),
            "permissions" to mapPermissions(role, permissionManager)
        )
    },
    "permissions" to mapPermissions(this, permissionManager)
)

fun Plugin.serialize(): Map<String, Any?> = mapOf(
    "name" to descriptor.name,
    "version" to descriptor.version,
    "author" to descriptor.author,
    "state" to state.order
)