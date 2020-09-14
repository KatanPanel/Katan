package me.devnatan.katan.api.server

import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.permission.PermissionHolder

interface ServerHolder : PermissionHolder {

    /**
     * The account that this holder is linked to.
     */
    val account: Account

    /**
     * The server that this holder is linked to.
     */
    val server: KServer

}