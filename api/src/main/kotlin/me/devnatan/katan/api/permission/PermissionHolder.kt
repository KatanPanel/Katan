package me.devnatan.katan.api.permission

import java.util.*

interface PermissionHolder {

    var permissions: EnumMap<Permission, PermissionFlag>

    /**
     * Checks whether we have a specific permission.
     * @param permission permission to be checked
     */
    fun hasPermission(permission: Permission): Boolean {
        if (permissions.isEmpty())
            return false;

        if (!permissions.containsKey(permission))
            return false;

        return permissions[permission]!! != PermissionFlags.NOT_ALLOWED
    }

}