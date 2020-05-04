package me.devnatan.katan.api.server

import me.devnatan.katan.api.account.KAccount
import me.devnatan.katan.api.permission.KPermissible
import me.devnatan.katan.api.permission.KPermission
import me.devnatan.katan.api.permission.named

/**
 * @property account the account that this holder is linked to.
 * @property isOwner if this holder owns the server he is linked to
 */
class KServerHolder(
    val account: KAccount,
    var isOwner: Boolean
) : KPermissible {

    companion object Permissions {

        /**
         * Whether the holder can access the server console.
         */
        val CONSOLE_ACCESS = KPermission(1) named "console_access"

        /**
         * Whether the holder can access the server file system (FTP).
         */
        val FTP_ACCESS = KPermission(2) named "ftp_access"

        /**
         * A map with all permissions and their names.
         */
        val ALL = arrayOf(CONSOLE_ACCESS, FTP_ACCESS)

    }

    /**
     * The level of permissions this holder has on the server.
     */
    override var permissions: Int = 0

    /**
     * Checks if the holder has a specific permission,
     * if [isOwner] is true it will be independent of the permission level.
     * @see KPermissible.hasPermission
     */
    override fun hasPermission(permission: KPermission): Boolean {
        return (isOwner) || super.hasPermission(permission)
    }

}