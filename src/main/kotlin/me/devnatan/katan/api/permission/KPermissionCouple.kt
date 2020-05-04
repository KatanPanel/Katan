package me.devnatan.katan.api.permission

import java.io.Serializable

/**
 * @property name the permission name
 * @property permission the permission itself
 */
class KPermissionCouple(
    val name: String,
    val permission: KPermission
) : Serializable {

    operator fun plus(permission: KPermissionCouple): KPermissionCouple {
        return KPermissionCouple(name, this.permission + permission.permission)
    }

    operator fun minus(permission: KPermissionCouple): KPermissionCouple {
        return KPermissionCouple(name, this.permission - permission.permission)
    }

}