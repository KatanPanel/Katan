package me.devnatan.katan.api.security.permission

/**
 * Represents a permission with the indirectly implicit value of a [PermissionFlag] by a [PermissionsHolder].
 * @property value the permission value.
 */
inline class Permission(val value: Int)