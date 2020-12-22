package me.devnatan.katan.api.security.permission

/**
 *
 */
class InheritedPermission(delegate: Permission, val inheritedFrom: PermissionsHolder): Permission by delegate