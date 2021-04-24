package me.devnatan.katan.api.server

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.permission.PermissionsHolder

/**
 * Represents an account that is connected to a server. This [account] has
 * permissions and contains specific access permissions to that [server].
 */
interface ServerHolder : PermissionsHolder {

    /**
     * Returns the [Account] that this holder is linked to.
     */
    val account: Account

    /**
     * Returns the [Server] that this holder is linked to.
     */
    val server: Server

    val isOwner: Boolean

}