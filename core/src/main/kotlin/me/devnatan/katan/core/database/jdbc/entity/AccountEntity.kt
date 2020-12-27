package me.devnatan.katan.core.database.jdbc.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.util.*

internal object AccountsTable : UUIDTable("katan_accounts") {

    val username = varchar("username", 255)
    val password = varchar("password", 255).nullable()
    val permissions = integer("permissions").default(0)
    val registeredAt = timestamp("registered_at")
    val lastLogin = timestamp("last_login").nullable()

}

class AccountEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AccountEntity>(AccountsTable)

    var username by AccountsTable.username
    var password by AccountsTable.password
    var permissions by AccountsTable.permissions
    var registeredAt by AccountsTable.registeredAt
    var lastLogin by AccountsTable.lastLogin

}