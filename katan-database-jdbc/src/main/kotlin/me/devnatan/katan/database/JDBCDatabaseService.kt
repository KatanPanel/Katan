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

package me.devnatan.katan.database

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.vendors.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

class JDBCDatabaseService : DatabaseService<Transaction>() {

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

    override val coroutineContext: CoroutineContext
        get() = error("JDBC database service cannot create coroutines with confined-scope")

    override suspend fun connect(settings: DatabaseSettings) {
        checkIsJdbcSettings(settings)
        require(settings.url != null || settings.dialect != null) {
            "No connection url or dialect provided (both null)"
        }

        close()
        database = Database.connect(
            url = settings.url ?: generateDialectUrl(settings),
            user = settings.user,
            password = settings.password
        ).apply { useNestedTransactions = true }

        _connected.value = true
    }

    fun isUrlSupported(url: String): Boolean {
        return url.substring(0, 4) == "jdbc"
    }

    private fun isDialectSupported(dialect: String): Boolean {
        return dialectUrlMappings.containsKey(dialect.toLowerCase())
    }

    override suspend fun close() {
        if (_connected.compareAndSet(expect = true, update = false))
            database.connector().close()
    }

    override suspend fun <R> transaction(
        dispatcher: CoroutineDispatcher,
        block: suspend Transaction.() -> R
    ): R = newSuspendedTransaction(dispatcher, database, statement = block)

    @OptIn(ExperimentalContracts::class)
    private fun checkIsJdbcSettings(settings: DatabaseSettings) {
        contract {
            returns() implies (settings is JDBCSettings)
        }

        if (settings !is JDBCSettings)
            throw IllegalArgumentException("Only JDBC Settings are supported")
    }

    @OptIn(ExperimentalContracts::class)
    private inline fun <reified T : DatabaseSettings> checkSettings(settings: DatabaseSettings) {
        contract {
            returns() implies (settings is T)
        }

        if (settings !is T)
            throw IllegalArgumentException("Unsupported settings type")
    }

    private fun generateDialectUrl(settings: DatabaseSettings): String {
        checkIsJdbcSettings(settings)

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
                url = url.replace("{file}", buildString {
                    append("./") // relative path
                    append(props["file"] ?: "katan")
                    if (dialect == H2Dialect.dialectName)
                        append(".h2")
                    append(".db")
                })
            }
            SQLServerDialect.dialectName -> {
            }
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