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

package me.devnatan.katan.database.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerFactory
import me.devnatan.katan.database.DatabaseService
import me.devnatan.katan.database.entity.JDBCServerEntity
import me.devnatan.katan.database.entity.JDBCServersTable
import me.devnatan.katan.database.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction

class JDBCServersRepository(
    private val database: DatabaseService<Transaction>,
    private val serverFactory: ServerFactory
) : ServersRepository {

    override suspend fun init() {
        database.transaction(Dispatchers.Unconfined) {
            SchemaUtils.create(
                JDBCServersTable,
                JDBCServersTable.HoldersTable,
                JDBCServersTable.CompositionsTable
            )
        }
    }

    override suspend fun findAll(): List<Server> {
        return database.transaction {
            JDBCServerEntity.all().map { entity ->
                serverFactory.create(entity.id.value, entity.containerId, entity.host, entity.port).apply {
                    name = entity.name
                }
            }

            /* ServerEntity.all().map { entity ->
                ServerDTO(
                    entity.id.value,
                    entity.name,
                    entity.containerId,
                    entity.gameType,
                    entity.gameVersion,
                    entity.host,
                    entity.port,
                    ServerHolderEntity.find {
                        JDBCServersTable..server eq entity.id
                    }.map { ServerHolderDTO(it.account.value, it.isOwner) },
                    ServerCompositionEntity.find {
                        ServerCompositionsTable.server eq entity.id
                    }.map { it.key }
                )
            } */
        }
    }

    override suspend fun insert(server: Server) {
        database.transaction {
            JDBCServerEntity.new(server.id) {
                this.name = server.name
                this.containerId = server.container.id
                this.gameType = server.game.game.id
                this.gameVersion = server.game.version?.id
                this.host = server.host
                this.port = server.port.toInt()
            }

            suspendedTransaction {
                val serverEntityId = JDBCServersTable.select {
                    JDBCServersTable.id eq server.id
                }.single()[JDBCServersTable.id]

                JDBCServersTable.CompositionsTable.batchInsert(
                    server.compositions,
                    shouldReturnGeneratedValues = false
                ) { composition ->
                    this[JDBCServersTable.CompositionsTable.key] = composition.key.name
                    this[JDBCServersTable.CompositionsTable.server] = serverEntityId
                }
            }
        }
    }

    override suspend fun update(server: Server) {
        TODO("Not yet implemented")
    }

}