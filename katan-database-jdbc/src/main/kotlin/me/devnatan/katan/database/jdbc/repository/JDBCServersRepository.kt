package me.devnatan.katan.database.jdbc.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.database.dto.server.ServerDTO
import me.devnatan.katan.database.dto.server.ServerHolderDTO
import me.devnatan.katan.database.jdbc.JDBCConnector
import me.devnatan.katan.database.jdbc.entity.*
import me.devnatan.katan.database.repository.ServersRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class JDBCServersRepository(private val connector: JDBCConnector) : ServersRepository {

    override suspend fun listServers(): List<ServerDTO> = connector.wrap("servers.list") {
        newSuspendedTransaction {
            ServerEntity.all().map { entity ->
                ServerDTO(
                    entity.id.value,
                    entity.name,
                    entity.containerId,
                    entity.gameType,
                    entity.gameVersion,
                    entity.host,
                    entity.port,
                    ServerHolderEntity.find {
                        ServerHoldersTable.server eq entity.id
                    }.map { ServerHolderDTO(it.account.value, it.isOwner) },
                    ServerCompositionEntity.find {
                        ServerCompositionsTable.server eq entity.id
                    }.map { it.key }
                )
            }
        }
    }

    override suspend fun insertServer(server: ServerDTO) = connector.wrap("servers.insert") {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            val serverId = ServerEntity.new(server.id) {
                this.name = server.name
                this.containerId = server.containerId
                this.gameType = server.game
                this.gameVersion = server.gameVersion
                this.host = server.host
                this.port = server.port
            }.id

            for (composition in server.compositions) {
                ServerCompositionEntity.new {
                    this.key = composition
                    this.server = serverId
                }
            }
        }
    }

}