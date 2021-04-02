package me.devnatan.katan.api.security.permission

interface PermissionManager {

    fun getRegisteredPermissionKeys(): Collection<PermissionKey>

    suspend fun registerPermissionKey(key: PermissionKey)

    suspend fun unregisterPermissionKey(key: PermissionKey)

}