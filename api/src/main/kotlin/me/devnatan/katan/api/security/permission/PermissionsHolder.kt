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
     * Returns the value of a permission or null if the permission is not found.
     * @param permission the permission.
     */
    fun getPermission(permission: Permission): PermissionFlag? {
        return permissions[permission]
    }

    /**
     * Returns if the permission [permission] is registered for that holder.
     * Use [isPermissionAllowed], [isPermissionNotAllowed] or [isPermissionInherited] to check your condition.
     * @param permission the permission to be checked.
     */
    fun hasPermission(permission: Permission): Boolean {
        return permissions.containsKey(permission)
    }

    /**
     * Set the [permission] value to the specified [value].
     * @param permission the permission to be set.
     * @param value the permission value.
     */
    fun setPermission(permission: Permission, value: PermissionFlag)

    /**
     * Returns `true` if [permission] is allowed.
     * @param permission the permission to be checked.
     */
    fun isPermissionAllowed(permission: Permission): Boolean {
        return when (getPermission(permission) ?: return false) {
            is PermissionFlag.Allowed -> true
            is PermissionFlag.NotAllowed -> false
            is PermissionFlag.Inherit -> throw UnsupportedOperationException("Cannot check inherited permissions.")
        }
    }

    /**
     * Returns `true` if [permission] is NOT allowed.
     * @param permission the permission to be checked.
     */
    fun isPermissionNotAllowed(permission: Permission): Boolean {
        return when (getPermission(permission) ?: return true) {
            is PermissionFlag.Allowed -> false
            is PermissionFlag.NotAllowed -> true
            is PermissionFlag.Inherit -> throw UnsupportedOperationException("Cannot check inherited permissions.")
        }
    }

}

/**
 * Returns `true` if permission is inherited.
 * @param permission the permission to be checked
 */
fun PermissionsHolder.isPermissionInherited(permission: Permission): Boolean {
    return getPermission(permission) is PermissionFlag.Inherit
}