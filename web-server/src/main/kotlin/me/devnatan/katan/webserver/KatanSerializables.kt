@file:OptIn(UnstableKatanApi::class)

package me.devnatan.katan.webserver

import io.ktor.http.*
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.isAllowed
import me.devnatan.katan.api.security.permission.isNotAllowed
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.get

@OptIn(UnstableKatanApi::class)
fun Server.serialize(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "state" to state,
    "target" to target,
    "compositions" to compositions.map { it.factory[it.key] },
    "container" to mapOf(
        "id" to container.id,
        "is-inspected" to container.isInspected(),
        "inspection" to container.inspection
    ),
    "query" to mapOf(
        "was-queried" to query.wasQueried(),
        "last-queried" to query.getLastQueried()?.toHttpDateString(),
        "latency" to query.getLatency(),
        "data" to query.data()
    )
)

fun Account.serialize(): Map<String, Any?> = mapOf(
    "id" to id,
    "username" to username,
    "registered-at" to registeredAt.toHttpDateString(),
    "role" to role?.let { role ->
        mapOf(
            "id" to role.id,
            "name" to role.name,
            "created-at" to role.createdAt.toHttpDateString()
        )
    },
    "permissions" to permissions.mapKeys { (permission, _) ->
        permission.value
    }.mapValues { (_, value) ->
        when {
            value.isAllowed() -> "allowed"
            value.isNotAllowed() -> "unallowed"
            else -> "inherit"
        }
    }
)
