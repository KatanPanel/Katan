/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.devnatan.katan.database.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object JDBCServersTable : IntIdTable("katan_servers") {

    internal object HoldersTable : IntIdTable("katan_servers_holders") {

        val account = reference("account", JDBCAccountsTable)
        val server = reference("server", JDBCServersTable)
        val permissions = integer("permissions")
        val isOwner = bool("is_owner")

    }

    internal object CompositionsTable : IntIdTable("katan_servers_compositions") {

        val key = varchar("name", 255)
        val server = reference("server", JDBCServersTable)

    }

    val name = varchar("name", 255)
    val containerId = varchar("container_id", 255)
    val gameType = varchar("game_type", 255)
    val gameVersion = varchar("game_version", 255).nullable()
    val host = varchar("host", 255)
    val port = integer("port")

}

class JDBCServerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<JDBCServerEntity>(JDBCServersTable)

    class HolderEntity(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<HolderEntity>(JDBCServersTable.HoldersTable)

        var account by JDBCServersTable.HoldersTable.account
        var server by JDBCServersTable.HoldersTable.server
        var permissions by JDBCServersTable.HoldersTable.permissions
        var isOwner by JDBCServersTable.HoldersTable.isOwner

    }

    class CompositionEntity(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<CompositionEntity>(JDBCServersTable.CompositionsTable)

        var key by JDBCServersTable.CompositionsTable.key
        var server by JDBCServersTable.CompositionsTable.server

    }

    var name by JDBCServersTable.name
    var containerId by JDBCServersTable.containerId
    var gameType by JDBCServersTable.gameType
    var gameVersion by JDBCServersTable.gameVersion
    var host by JDBCServersTable.host
    var port by JDBCServersTable.port

    val holders by HolderEntity referrersOn JDBCServersTable.HoldersTable.server
    val compositions by CompositionEntity referrersOn JDBCServersTable.CompositionsTable.server

}