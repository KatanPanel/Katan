package me.devnatan.katan.core.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object ServerHoldersTable : IntIdTable("katan_server_holders") {

    val account     = reference("account", AccountsTable)
    val server      = reference("server", ServersTable)
    val permissions = integer("permissions")
    val isOwner     = bool("is_owner")

}

class ServerHolderEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServerHolderEntity>(ServerHoldersTable)

    var account     by ServerHoldersTable.account
    var server      by ServerHoldersTable.server
    var permissions by ServerHoldersTable.permissions
    var isOwner     by ServerHoldersTable.isOwner

}