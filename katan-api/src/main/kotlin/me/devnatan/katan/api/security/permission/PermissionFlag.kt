package me.devnatan.katan.api.security.permission

/**
 * A permission flag represents the value of specific a permission for [PermissionsHolder].
 * They are used to determine the access of protected properties.
 * @property code flag identification code.
 */
enum class PermissionFlag(val code: Int) {

    /**
     * The holder has permission from the current context and it is allowed.
     */
    ALLOWED(1),

    /**
     * The holder does not have permission from the current context or it is not allowed.
     */
    NOT_ALLOWED(-1)

}

/**
 * Returns `true` if this flag has a positive value.
 */
fun PermissionFlag.isAllowed(): Boolean {
    return this == PermissionFlag.ALLOWED
}

/**
 * Returns `true` if this flag has a negative value.
 */
fun PermissionFlag.isNotAllowed(): Boolean {
    return this == PermissionFlag.NOT_ALLOWED
}

/**
 * Returns the inverse value of this permission flag.
 */
fun PermissionFlag.not(): PermissionFlag {
    return if (this == PermissionFlag.ALLOWED) PermissionFlag.NOT_ALLOWED
    else PermissionFlag.ALLOWED
}