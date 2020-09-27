package me.devnatan.katan.core.repository

import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.database.jdbc.entity.ServerEntity
import me.devnatan.katan.core.server.NoOpServerContainer
import me.devnatan.katan.core.server.ServerHolderImpl
import me.devnatan.katan.core.server.SerializableServer
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface ServersRepository {

    suspend fun listServers(): List<Server>

    suspend fun insertServer(server: Server)

}

class JDBCServersRepository(private val core: KatanCore, private val connector: JDBCConnector) : ServersRepository {

    override suspend fun listServers(): List<Server> {
        return newSuspendedTransaction(Dispatchers.Default, connector.database) {
            ServerEntity.all().map { entity ->
                SerializableServer(entity.id.value,
                    entity.name,
                    entity.address,
                    entity.port,
                    entity.composition
                ).apply {
                    container = NoOpServerContainer(entity.containerId)

                    holders.addAll(entity.holders.mapNotNull {
                        /*
                            If the account is null this is a database synchronization error
                            we can ignore this, but in the future we should alert that kind of thing.
                         */
                        core.accountManager.getAccount(it.account.value.toString())
                    }.map { ServerHolderImpl(it, this) })
                }
            }
        }
    }

    override suspend fun insertServer(server: Server) {
        newSuspendedTransaction(Dispatchers.Default, connector.database) {
            ServerEntity.new(server.id) {
                this.name = server.name
                this.address = server.address
                this.port = server.port
                this.containerId = server.container.id
            }
        }
    }

}