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
     */
    object Inherit : PermissionFlag(3)

}

/**
 * Returns `true` if this flag has a positive value.
 * @throws UnsupportedOperationException if is [PermissionFlag.Inherit].
 */
fun PermissionFlag.allowed(): Boolean {
    return when (this) {
        is PermissionFlag.Allowed -> true
        is PermissionFlag.NotAllowed -> false
        else -> throw UnsupportedOperationException("Cannot check inherited permission flag value.")
    }
}

/**
 * Returns `true` if this flag has a negative value.
 * @throws UnsupportedOperationException if is [PermissionFlag.Inherit].
 */
fun PermissionFlag.notAllowed(): Boolean {
    return when (this) {
        is PermissionFlag.Allowed -> true
        is PermissionFlag.NotAllowed -> false
        else -> throw UnsupportedOperationException("Cannot check inherited permission flag value.")
    }
}

/**
 * Returns the inverse value of this permission flag, or `this` for [PermissionFlag.Inherit].
 */
fun PermissionFlag.not(): PermissionFlag {
    return when (this) {
        is PermissionFlag.Allowed -> PermissionFlag.NotAllowed
        is PermissionFlag.NotAllowed -> PermissionFlag.Allowed
        else -> this
    }
}