package me.devnatan.katan

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import io.ktor.application.Application
import io.ktor.application.log
import me.devnatan.katan.core.manager.AccountManager
import me.devnatan.katan.core.manager.ServerManager
import me.devnatan.katan.core.manager.WSManager
import me.devnatan.katan.core.sql.AccountEntity
import me.devnatan.katan.core.sql.AccountsTable
import me.devnatan.katan.core.sql.ServersTable
import me.devnatan.katan.core.util.fromString
import me.devnatan.katan.core.util.readResource
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.exitProcess

class Katan(private val app: Application) {

    val webSocketManager = WSManager(this)
    lateinit var accountManager: AccountManager
    lateinit var serverManager: ServerManager

    internal lateinit var config: KatanConfig
    internal lateinit var database: Database
    private lateinit var environment: String
    lateinit var router: KatanRouter
    lateinit var docker: DockerClient
    lateinit var jsonMapper: ObjectMapper

    init {
        EventBus.getDefault().register(webSocketManager)
    }

    fun boot() {
        config = KatanConfig(readResource("katan.json").fromString(jsonMapper)!!)
        environment = config["env"]
        docker = DockerClientBuilder.getInstance(DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(config.get<String>("docker", "host")).build()).build();
        connectDatabase()
        loadServers()
        loadAccounts()
    }

    private fun connectDatabase() {
        val mysql = config.get<Map<*, *>>("mysql")
        database = Database.connect(
            mysql["url"] as String,
            mysql["driver"] as String,
            mysql["user"] as String,
            mysql["password"] as String
        )

        transaction(database) {
            try {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(
                    AccountsTable,
                    ServersTable
                )
            } catch (e: Throwable) {
                app.log.error("Couldn't connect to database, please verify your credentials.")
                exitProcess(0)
            }
        }
    }

    private fun loadServers() {
        serverManager = ServerManager(this)
        serverManager.loadServers()
    }

    private fun loadAccounts() {
        accountManager = AccountManager(this)
        transaction {
            for (account in AccountEntity.all()) {
                accountManager.createAccount(account.username, account.password)
            }
        }
    }

}