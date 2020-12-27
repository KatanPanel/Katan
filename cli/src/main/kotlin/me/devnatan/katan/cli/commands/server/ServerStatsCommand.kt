package me.devnatan.katan.cli.commands.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import me.devnatan.katan.api.server.*
import me.devnatan.katan.cli.KatanCLI
import me.devnatan.katan.cli.err
import me.devnatan.katan.cli.render
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ALIAS_SERVER_STATS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_HELP_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_ARG_LABEL_SERVER_NAME
import me.devnatan.katan.common.KatanTranslationKeys.CLI_HELP_SERVER_STATS
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_CPU
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_CPU_PROCESS_USAGE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_MEMORY
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_MEMORY_CACHE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_MEMORY_LIMIT
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_MEMORY_TOTAL
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_MEMORY_USAGE
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_LOGS_SINGLE_CPU
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_FOUND
import me.devnatan.katan.common.KatanTranslationKeys.CLI_SERVER_NOT_RUNNING
import me.devnatan.katan.common.util.toFileSizeFormat

class ServerStatsCommand(private val cli: KatanCLI) : CliktCommand(
    name = cli.translate(CLI_ALIAS_SERVER_STATS),
    help = cli.translate(CLI_HELP_SERVER_STATS)
) {

    private val serverName by argument(
        cli.translate(CLI_ARG_LABEL_SERVER_NAME),
        cli.translate(CLI_ARG_HELP_SERVER_NAME)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run() {
        val server = cli.serverManager.getServerOrNull(serverName)
            ?: return err(cli.translate(CLI_SERVER_NOT_FOUND, serverName))

        if (!server.state.isActive())
            return err(cli.translate(CLI_SERVER_NOT_RUNNING, server.name))

        val job = cli.coroutineScope.async(Dispatchers.IO, CoroutineStart.UNDISPATCHED) {
            cli.serverManager.getServerStats(server)
        }

        job.invokeOnCompletion {
            val stats = job.getCompleted()
            render(table {
                cellStyle {
                    paddingLeft = 1
                    paddingRight = 1
                    borderLeft = true
                    borderRight = true
                }

                header {
                    row {
                        cell(cli.translate(CLI_SERVER_LOGS_MEMORY)) {
                            alignment = TextAlignment.BottomCenter
                            columnSpan = 5
                        }
                    }
                    row(
                        cli.translate(CLI_SERVER_LOGS_MEMORY_USAGE),
                        "%",
                        cli.translate(CLI_SERVER_LOGS_MEMORY_TOTAL),
                        cli.translate(CLI_SERVER_LOGS_MEMORY_CACHE),
                        cli.translate(CLI_SERVER_LOGS_MEMORY_LIMIT)
                    )
                }
                body {
                    row(
                        stats.getUsedMemory().toFileSizeFormat(),
                        "${String.format("%.2f", stats.getMemoryUsagePercentage())}%",
                        stats.memoryMaxUsage.toFileSizeFormat(),
                        stats.memoryCache.toFileSizeFormat(),
                        stats.memoryLimit.toFileSizeFormat()
                    )
                }
            })

            echo()
            render(table {
                cellStyle {
                    paddingLeft = 1
                    paddingRight = 1
                    borderLeft = true
                    borderRight = true
                }

                header {
                    cellStyle { alignment = TextAlignment.BottomCenter }
                    row {
                        cell(cli.translate(CLI_SERVER_LOGS_CPU, stats.onlineCpus)) {
                            columnSpan = 2
                        }
                    }

                    row {
                        cell("#")
                        cell(cli.translate(CLI_SERVER_LOGS_CPU_PROCESS_USAGE))
                    }
                }

                body {
                    cellStyle { alignment = TextAlignment.BottomLeft }
                    for ((cpu, usage) in stats.perCpuUsage.withIndex())
                        row {
                            cell(cli.translate(CLI_SERVER_LOGS_SINGLE_CPU, cpu)) {
                                alignment = TextAlignment.BottomRight
                            }
                            cell(String.format("%.2f", stats.getCpuUsagePercentage(usage)) + "%")
                        }
                }
            })
        }
    }

}