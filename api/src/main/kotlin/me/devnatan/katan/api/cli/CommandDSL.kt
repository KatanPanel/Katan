package me.devnatan.katan.api.cli

import kotlinx.coroutines.CoroutineDispatcher

typealias CommandExecutor = suspend Command.(label: String, args: Array<out String>) -> Unit

private class KatanCommandImpl(
    name: String,
    aliases: List<String>,
    private inline val executor: CommandExecutor
): KatanCommand(name, aliases) {

    override suspend fun execute(label: String, args: Array<out String>) {
        executor(label, args)
    }

}

class CommandBuilder(val name: String) {

    var aliases: List<String> = emptyList()
    var dispatcher: CoroutineDispatcher? = null
    var executor: CommandExecutor? = null
    var commands: MutableList<CommandBuilder> = arrayListOf()

    fun toCommand(): Command {
        return KatanCommandImpl(name, aliases, executor ?: { _, _ -> }).apply {
            for (subcommand in commands)
                addCommand(subcommand.toCommand())
        }
    }

    fun execute(block: CommandExecutor) {
        this.executor = block
    }

    inline fun command(name: String, vararg aliases: String, crossinline block: CommandBuilder.() -> Unit) {
        command(name, aliases.toList(), block).also { commands.add(it) }
    }

}

inline fun command(name: String, aliases: List<String>, crossinline block: CommandBuilder.() -> Unit): CommandBuilder {
    return CommandBuilder(name).apply {
        this.aliases = aliases.toList()
    }.apply(block)
}

inline fun command(name: String, vararg aliases: String, crossinline block: CommandBuilder.() -> Unit): Command {
    return command(name, aliases.toList(), block).toCommand()
}