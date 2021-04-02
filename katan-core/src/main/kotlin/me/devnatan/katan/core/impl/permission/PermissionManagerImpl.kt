package me.devnatan.katan.core.impl.permission

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.api.security.permission.PermissionKey
import me.devnatan.katan.api.security.permission.PermissionManager

class PermissionManagerImpl : PermissionManager {

    private val keys: MutableCollection<PermissionKey> = hashSetOf()
    private val mutex = Mutex()

    override fun getRegisteredPermissionKeys(): Collection<PermissionKey> {
        return keys
    }

    override suspend fun registerPermissionKey(key: PermissionKey) {
        mutex.withLock { keys.add(key) }
    }

    override suspend fun unregisterPermissionKey(key: PermissionKey) {
        mutex.withLock { keys.remove(key) }
    }

}