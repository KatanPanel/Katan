package me.devnatan.katan.cli.commands.plugin

import com.github.ajalt.clikt.core.CliktCommand
import me.devnatan.katan.api.plugin.PluginState
import me.devnatan.katan.cli.KatanCLI

class PluginListCommand(private val cli: KatanCLI) : CliktCommand(
    name = "ls",
    help = "Lists loaded plugins."
) {

    override fun run() {
        val plugins = cli.pluginManager.getPlugins()
        echo("Loaded plugins (${plugins.size}):")
        for (plugin in plugins.sortedBy { it.state }) {
            echo(
                "${plugin.descriptor.name} v${plugin.descriptor.version} - ${
                    when (plugin.state) {
                        is PluginState.Loaded -> "loaded (at ${(plugin.state as PluginState.Loaded).loadedAt})"
                        is PluginState.Started -> "started (at ${(plugin.state as PluginState.Started).startedAt})"
                        is PluginState.Disabled -> "stopped (at ${(plugin.state as PluginState.Disabled).disabledAt})"
                        else -> "Unknown state"
                    }
                }"
            )
        }
    }

}