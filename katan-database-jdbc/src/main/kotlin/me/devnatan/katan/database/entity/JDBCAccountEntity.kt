package me.devnatan.katan.database.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.util.*

internal object JDBCAccountsTable : UUIDTable("katan_accounts") {

    val username = varchar("username", 255)
    val password = varchar("password", 255).nullable()
    val permissions = integer("permissions").default(0)
    val registeredAt = timestamp("registered_at")
    val lastLogin = timestamp("last_login").nullable()

}

class JDBCAccountEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<JDBCAccountEntity>(JDBCAccountsTable)

    var username by JDBCAccountsTable.username
    var password by JDBCAccountsTable.password
    var permissions by JDBCAccountsTable.permissions
    var registeredAt by JDBCAccountsTable.registeredAt
    var lastLogin by JDBCAccountsTable.lastLogin

}