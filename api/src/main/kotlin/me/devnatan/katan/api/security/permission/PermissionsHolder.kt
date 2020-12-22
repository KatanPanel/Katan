package me.devnatan.katan.api.security.permission

/**
 * Represents an object that contains a permissions list.
 */
interface PermissionsHolder {

    /**
     * Permission map and permission values for this holder,
     * the key being the permission and the value the [PermissionFlag].
     */
    val permissions: List<Permission>

    /**
     * Returns a [Permission] or null if the permission is not found.
     * @param key the permission key.
     */
    fun getPermission(key: PermissionKey): Permission? {
        return permissions.find { it.key == key }
    }

    /**
     * Returns if the permission [key] is registered for that holder.
     * @param key the permission to be checked.
     */
    fun hasPermission(key: PermissionKey): Boolean {
        return permissions.any { it.key == key }
    }

    /**
     * Set the [Permission] value of the specified [key] to the specified [value].
     * @param key the permission to be set.
     * @param value the permission value.
     * @return the modified permission
     */
    fun setPermission(key: PermissionKey, value: PermissionFlag): Permission

}

fun PermissionsHolder.getPermissionValue(key: PermissionKey): PermissionFlag {
    return getPermission(key)?.value ?: throw NoSuchElementException(key.toString())
}