package me.devnatan.katan.api.permission

typealias PermissionFlag = Int

object PermissionFlags {

    val ALL: Array<PermissionFlag> by lazy {
        arrayOf(NOT_ALLOWED, ALLOWED, INHERIT)
    }

    const val NOT_ALLOWED: Int = 0
    const val ALLOWED: Int = 1
    const val INHERIT: Int = 2

}