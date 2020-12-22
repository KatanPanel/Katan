package me.devnatan.katan.core.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.get
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.database.jdbc.entity.ServerCompositionEntity
import me.devnatan.katan.core.database.jdbc.entity.ServerEntity
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface ServersRepository {

    suspend fun listServers(context: suspend (List<ServerEntity>) -> Unit)

    suspend fun insertServer(server: Server)

}

class JDBCServersRepository(private val connector: JDBCConnector) : ServersRepository {

    override suspend fun listServers(context: suspend (List<ServerEntity>) -> Unit) {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            context(ServerEntity.all().toList())
        }
    }

    @OptIn(UnstableKatanApi::class)
    override suspend fun insertServer(server: Server) {
        newSuspendedTransaction(Dispatchers.IO, connector.database) {
            val serverId = ServerEntity.new(server.id) {
                this.name = server.name
                this.containerId = server.container.id
                this.gameType = server.game.type.name
                this.gameVersion = server.game.version?.name
                this.host = server.host
                this.port = server.port.toInt()
            }.id

            for (composition in server.compositions) {
                ServerCompositionEntity.new {
                    this.key = composition.factory[composition.key]!!
                    this.server = serverId
                }
            }
        }
    }

}