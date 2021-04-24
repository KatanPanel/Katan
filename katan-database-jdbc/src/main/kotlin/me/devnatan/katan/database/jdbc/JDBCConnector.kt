package me.devnatan.katan.database.jdbc

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.database.DatabaseConnector
import me.devnatan.katan.database.DatabaseQueryHandler
import me.devnatan.katan.database.DatabaseRepositories
import me.devnatan.katan.database.DatabaseSettings
import me.devnatan.katan.database.jdbc.entity.AccountsTable
import me.devnatan.katan.database.jdbc.entity.ServerCompositionsTable
import me.devnatan.katan.database.jdbc.entity.ServerHoldersTable
import me.devnatan.katan.database.jdbc.entity.ServersTable
import me.devnatan.katan.database.jdbc.repository.JDBCAccountsRepository
import me.devnatan.katan.database.jdbc.repository.JDBCServersRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.vendors.*

class JDBCConnector(
    override val queryHandler: DatabaseQueryHandler
) : DatabaseConnector {

    companion object {

        val dialectUrlMappings = mapOf(
            PostgreSQLDialect.dialectName to "jdbc:postgresql://{host}/{database}",
            PostgreSQLNGDialect.dialectName to "jdbc:pgsql://{host}/{database}",
            MysqlDialect.dialectName to "jdbc:mysql://{host}/{database}",
            OracleDialect.dialectName to "jdbc:jdbc:oracle:thin:@//{host}/{database}",
            SQLiteDialect.dialectName to "jdbc:sqlite:{file}",
            H2Dialect.dialectName to "jdbc:h2:{file}",
            SQLServerDialect.dialectName to "jdbc:sqlserver://{host};databaseName={database}"
        )

    }

    private val _connected = atomic(false)
    lateinit var database: Database private set
    override val repositories = jdbcRepositories()

    override suspend fun connect(settings: DatabaseSettings) {
        require(settings.url != null || settings.dialect != null) {
            "No connection url or dialect provided (both null)"
        }

        close()
        database = Database.connect(
            url = settings.url ?: generateDialectUrl(settings),
            user = settings.user,
            password = settings.password
        )

        newSuspendedTransaction(Dispatchers.Default, database) {
            SchemaUtils.create(
                AccountsTable,
                ServersTable,
                ServerHoldersTable,
                ServerCompositionsTable
            )
        }

        _connected.value = true
    }

    override fun isUrlSupported(url: String): Boolean {
        return url.substring(0, 4) == "jdbc"
    }

    override fun isDialectSupported(dialect: String): Boolean {
        return dialectUrlMappings.containsKey(dialect.toLowerCase())
    }

    override fun isConnected(): Boolean {
        return _connected.value
    }

    override suspend fun close() {
        if (_connected.compareAndSet(expect = true, update = false))
            database.connector().close()
    }

    private fun jdbcRepositories() = DatabaseRepositories(
        JDBCAccountsRepository(this),
        JDBCServersRepository(this)
    )

    private fun generateDialectUrl(settings: DatabaseSettings): String {
        val dialect = settings.dialect!!
        require(isDialectSupported(dialect)) {
            "Unsupported database dialect: $dialect"
        }

        var url = dialectUrlMappings.getValue(dialect)
            .replace("{host}", settings.host)
            .replace("{database}", settings.database)

        val props = settings.properties
        when (dialect) {
            SQLiteDialect.dialectName, H2Dialect.dialectName -> {
                require(props.containsKey("file")) {
                    "Missing \"file\" database property for \"$dialect\" dialect"
                }

                url = url.replace("{file}", props.getValue("file"))
            }
            SQLServerDialect.dialectName -> {}
            else -> if (props.isNotEmpty()) {
                val literal = props.map { (key, value) ->
                    "$key=$value"
                }.joinToString("&")

                url += "?$literal"
            }
        }

        return url
    }

}