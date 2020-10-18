package me.devnatan.katan.api.security.permission

/**
 * A permission flag represents the value of specific a permission for [PermissionsHolder].
 * They are used to determine the access of protected properties.
 * @property code flag identification code.
 */
sealed class PermissionFlag constructor(val code: Int) {

    /**
     * The holder has permission from the current context and it is allowed for him.
     * It is the inverse of [NotAllowed].
     */
    object Allowed : PermissionFlag(1)

    /**
     * The holder does not have permission from the current context or it is not allowed for him.
     * It is the inverse of [Allowed].
     */
    object NotAllowed : PermissionFlag(2)

    /**
     * Will use the flag of the [PermissionsHolder]’s parent.
     * By default, if used as a value, it will be as if it were a [NotAllowed].
     */
    class Inherit(val inherit: PermissionFlag) : PermissionFlag(3)

}

/**
 * Returns `true` if this flag has a positive value, also checking the
 * inherited value of the [PermissionFlag.Inherit] flag recursively if it is required.
 */
fun PermissionFlag.allowed(): Boolean {
    return when (this) {
        is PermissionFlag.Allowed -> true
        is PermissionFlag.NotAllowed -> false
        is PermissionFlag.Inherit -> inherit.allowed()
    }
}

/**
 * Returns the inverse value of this permission flag.
 */
fun PermissionFlag.not(): PermissionFlag {
    return when (this) {
        is PermissionFlag.Allowed -> PermissionFlag.NotAllowed
        is PermissionFlag.NotAllowed -> PermissionFlag.Allowed
        is PermissionFlag.Inherit -> this
    }
}