package me.devnatan.katan.api.permission

interface KPermissible {

    var permissions: Int

    /**
     * Checks whether we have a specific permission.
     * @param permission permission to be checked
     */
    fun hasPermission(permission: KPermission): Boolean {
        return (permissions == -1) || (permissions and permission.value) == 0
    }

    /**
     * Checks whether we have a specific permission.
     * @see hasPermission
     */
    fun hasPermission(permission: KPermissionCouple): Boolean {
        return hasPermission(permission.permission)
    }

}