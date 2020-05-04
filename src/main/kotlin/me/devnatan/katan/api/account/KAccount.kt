package me.devnatan.katan.api.account

import me.devnatan.katan.api.permission.KPermissible
import me.devnatan.katan.api.permission.KPermission
import me.devnatan.katan.api.permission.named
import java.util.*

interface KAccount : KPermissible {

    companion object Permissions {

        /**
         * If the account is allowed to create new servers
         */
        val CREATE_SERVERS = KPermission(1) named "create_server"

        /**
         * A map with all permissions and their names.
         */
        val ALL = arrayOf(CREATE_SERVERS)

    }

    /**
     * Account ID
     */
    val id: UUID

    /**
     * Account username.
     */
    val username: String

}