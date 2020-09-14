package me.devnatan.katan.core.sql

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.GlobalStatementInterceptor
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.statements.expandArgs
import org.slf4j.Logger

class GlobalSQLLogger(private val logger: Logger) : GlobalStatementInterceptor {

    override fun afterExecution(
        transaction: Transaction,
        contexts: List<StatementContext>,
        executedStatement: PreparedStatementApi
    ) {
        for (ctx in contexts)
            logger.debug(ctx.expandArgs(transaction))
    }

}