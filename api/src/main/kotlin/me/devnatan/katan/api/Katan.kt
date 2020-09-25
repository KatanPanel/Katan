package me.devnatan.katan.api

import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.api.manager.ServerManager

interface Katan {

    val accountManager: AccountManager

    val serverManager: ServerManager

}