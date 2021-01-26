package me.devnatan.katan.api.security.permission

import me.devnatan.katan.api.security.permission.PermissionKeyType.ACCOUNT
import me.devnatan.katan.api.security.permission.PermissionKeyType.ROLE
import me.devnatan.katan.api.security.permission.PermissionKeyType.SERVER_HOLDER

interface PermissionKey {

    val code: Byte

    val name: String

    val type: Int

}

fun PermissionKey.isTypeOf(keyType: Int): Boolean {
    return type and keyType > 0
}

object PermissionKeyType {

    const val ACCOUNT = 1
    const val ROLE = 2
    const val SERVER_HOLDER = 4

}

private class PermissionKeyImpl(
    override val code: Byte,
    override val name: String,
    override val type: Int = ACCOUNT or ROLE or SERVER_HOLDER
) : PermissionKey

object DefaultPermissionKeys {

    private val ADD_SERVERS: PermissionKey = PermissionKeyImpl(1, "add_servers", ACCOUNT or ROLE)
    private val DELETE_SERVERS: PermissionKey = PermissionKeyImpl(2, "delete_servers", ACCOUNT or ROLE)
    private val ACCESS_SERVER_CONSOLE: PermissionKey = PermissionKeyImpl(3, "access_server_console", SERVER_HOLDER)
    private val ACCESS_SERVER_FILES: PermissionKey = PermissionKeyImpl(4, "access_server_fs", SERVER_HOLDER)
    private val VIEW_BACKEND_INFO: PermissionKey = PermissionKeyImpl(5, "view_backend_info", ACCOUNT or ROLE)

    val DEFAULTS: Array<PermissionKey> by lazy {
        arrayOf(ADD_SERVERS, DELETE_SERVERS, ACCESS_SERVER_CONSOLE, ACCESS_SERVER_FILES, VIEW_BACKEND_INFO)
    }

}