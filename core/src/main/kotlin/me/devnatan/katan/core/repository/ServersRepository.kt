package me.devnatan.katan.core.repository

import kotlinx.coroutines.Dispatchers
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
        newSuspendedTransaction(Dispatchers.Default, connector.database) {
            context(ServerEntity.all().toList())
        }
    }

    override suspend fun insertServer(server: Server) {
        newSuspendedTransaction(Dispatchers.Default, connector.database) {
            val serverId = ServerEntity.new(server.id) {
                this.name = server.name
                this.containerId = server.container.id
                this.target = server.target.game
            }.id

            for (composition in server.compositions) {
                ServerCompositionEntity.new {
                    this.key = composition.factory.get(composition.key)!!
                    this.server = serverId
                }
            }
        }
    }

}