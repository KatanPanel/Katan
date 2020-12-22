package me.devnatan.katan.core.impl.permission

import me.devnatan.katan.api.security.permission.PermissionKey
import me.devnatan.katan.api.security.permission.PermissionManager

class PermissionManagerImpl : PermissionManager {

    private val keys: MutableCollection<PermissionKey> = hashSetOf()

    override fun getRegisteredPermissionKeys(): Collection<PermissionKey> {
        return keys.toSet()
    }

    override fun registerPermissionKey(key: PermissionKey) {
        synchronized (keys) { keys.add(key) }
    }

    override fun unregisterPermissionKey(key: PermissionKey) {
        synchronized (keys) { keys.remove(key) }
    }

}