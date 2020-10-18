package me.devnatan.katan.api.security.permission

/**
 * Represents an object that contains a permissions list.
 */
interface PermissionsHolder {

    /**
     * Permission map and permission values for this holder,
     * the key being the permission and the value the [PermissionFlag].
     */
    val permissions: Map<Permission, PermissionFlag>

    /**
     * Returns whether permission [permission] is registered for that holder.
     * Use [isAllowed], [isNotAllowed] or [isInherited] to check your condition.
     * @param permission the permission to be checked.
     */
    fun hasPermission(permission: Permission): Boolean {
        return permissions.containsKey(permission)
    }

}

/**
 * Returns `true` if permission is allowed.
 * @param permission permission to be checked
 */
fun PermissionsHolder.isAllowed(permission: Permission): Boolean {
    val flag = permissions[permission] ?: return false
    return when (flag) {
        is PermissionFlag.Allowed -> true
        is PermissionFlag.NotAllowed -> false
        is PermissionFlag.Inherit -> flag.allowed()
    }
}

/**
 * Returns `true` if permission is not allowed.
 * @param permission permission to be checked
 */
fun PermissionsHolder.isNotAllowed(permission: Permission): Boolean {
    val flag = permissions[permission] ?: return true
    return when (flag) {
        is PermissionFlag.Allowed -> false
        is PermissionFlag.NotAllowed -> true
        is PermissionFlag.Inherit -> !flag.allowed()
    }
}

/**
 * Returns `true` if permission is inherited.
 * @param permission permission to be checked
 */
fun PermissionsHolder.isInherited(permission: Permission): Boolean {
    return permissions[permission] is PermissionFlag.Inherit
}