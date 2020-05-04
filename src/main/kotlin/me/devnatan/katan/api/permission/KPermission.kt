package me.devnatan.katan.api.permission

/**
 * @property value the permission value
 */
inline class KPermission(val value: Int) {

    operator fun plus(permission: KPermission): KPermission {
        return KPermission(value + permission.value)
    }

    operator fun plus(permission: Int): KPermission {
        return KPermission(value + permission)
    }

    operator fun minus(permission: KPermission): KPermission {
        return KPermission(value - permission.value)
    }

    operator fun minus(permission: Int): KPermission {
        return KPermission(value - permission)
    }

}

infix fun KPermission.named(name: String): KPermissionCouple {
    return KPermissionCouple(name, this)
}