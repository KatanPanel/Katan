package me.devnatan.katan.api.security.permission

interface PermissionManager {

    fun getRegisteredPermissionKeys(): Collection<PermissionKey>

    fun registerPermissionKey(key: PermissionKey)

    fun unregisterPermissionKey(key: PermissionKey)

}