package me.devnatan.katan.api.permission

interface PermissionHolder {

    var permissions: MutableMap<Permission, PermissionFlag>

    /**
     * Checks whether we have a specific permission.
     * @param permission permission to be checked
     */
    fun hasPermission(permission: Permission): Boolean {
        if (permissions.isEmpty())
            return false

        if (!permissions.containsKey(permission))
            return false

        return permissions.getValue(permission) != PermissionFlags.NOT_ALLOWED
    }

}