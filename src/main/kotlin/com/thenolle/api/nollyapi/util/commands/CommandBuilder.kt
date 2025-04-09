package com.thenolle.api.nollyapi.util.commands

import com.thenolle.api.nollyapi.util.msg.MessageColor
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Builder class used to define and configure commands for the plugin.
 *
 * This class allows you to define various aspects of a command such as its description, permission,
 * usage, cooldowns, subcommands, and more. It also supports both synchronous and asynchronous execution
 * of command logic, as well as tab completion and validation.
 *
 * @param name The name of the command being built.
 */
class CommandBuilder(val name: String) {
    var description: String = ""
    var permission: String? = null
    var permissionMessage: String = "&cYou do not have permission to use this command."
    var usage: String = "/$name"
    var aliases: List<String> = emptyList()
    var hidden: Boolean = false
    var category: String? = null

    private var async: Boolean = false
    private var cooldownMillis: Long? = null
    private var cooldownKey: ((CommandContext) -> String)? = null
    private var validation: (CommandContext.() -> Unit)? = null

    private var suggestions: ((CommandContext) -> List<String>)? = null
    private var suggestionsEnabled: Boolean = true

    private var executeBlock: (CommandContext) -> Unit = {}
    private val subCommands = mutableMapOf<String, CommandBuilder>()

    /**
     * Specifies the block of code to be executed when the command is run.
     *
     * @param block The block of code to run when the command is executed.
     */
    fun run(block: CommandContext.() -> Unit) {
        executeBlock = block
    }

    /**
     * Specifies an asynchronous block of code to be executed when the command is run.
     *
     * @param block The asynchronous block of code to run when the command is executed.
     */
    fun runAsync(block: suspend CommandContext.() -> Unit) {
        async = true
        executeBlock = { ctx ->
            Bukkit.getScheduler().runTaskAsynchronously(
                JavaPlugin.getProvidingPlugin(CommandBuilder::class.java), Runnable { runBlocking { block(ctx) } })
        }
    }

    /**
     * Defines a subcommand under the current command.
     *
     * @param name The name of the subcommand.
     * @param builder The block to configure the subcommand.
     */
    fun sub(name: String, builder: CommandBuilder.() -> Unit) {
        val child = CommandBuilder(name).apply(builder)
        subCommands[name.lowercase()] = child
    }

    /**
     * Specifies the suggestions for tab completion.
     *
     * @param block The block that returns a list of suggestions based on the [CommandContext].
     */
    fun suggest(block: (CommandContext) -> List<String>) {
        suggestions = block
    }

    /**
     * Disables suggestions for tab completion.
     */
    fun disableSuggestions() {
        suggestionsEnabled = false
    }

    /**
     * Suggests the names of all online players for tab completion.
     */
    fun suggestPlayers() = suggest { Bukkit.getOnlinePlayers().map { it.name } }

    /**
     * Sets a cooldown for the command.
     *
     * @param millis The duration of the cooldown in milliseconds.
     * @param key A function that generates a unique key for tracking the cooldown.
     */
    fun cooldown(millis: Long, key: ((CommandContext) -> String) = { it.sender.name }) {
        cooldownMillis = millis
        cooldownKey = key
    }

    /**
     * Generates help text for the command, including subcommands.
     *
     * @return A list of strings representing the command help.
     */
    fun generateHelp(): List<String> {
        val help = mutableListOf<String>()
        if (!hidden) help += "&e/$name &7- $description"
        for ((sub, command) in subCommands) if (!hidden) help += "&e/$name $sub &7- ${command.description}"
        return if (help.isEmpty()) help + "&cNo commands available." else help
    }

    /**
     * Defines validation logic to be executed before the command is run.
     *
     * @param block The block of validation logic to execute.
     */
    fun validate(block: CommandContext.() -> Unit) {
        validation = block
    }

    /**
     * Conditionally suggests tab completion options based on a condition.
     *
     * @param condition The condition to check before suggesting tab completion options.
     * @param block The block to provide suggestions if the condition is met.
     */
    fun suggestIf(condition: CommandContext.() -> Boolean, block: CommandContext.() -> List<String>) {
        val original = suggestions
        suggest { ctx ->
            if (ctx.condition()) block(ctx)
            else original?.invoke(ctx) ?: emptyList()
        }
    }

    /**
     * Suggests values from an enum for tab completion.
     *
     * @param ignoreCase Whether to ignore case when matching enum values.
     */
    inline fun <reified T : Enum<T>> suggestEnum(ignoreCase: Boolean = true) {
        val values = enumValues<T>().map { it.name }
        suggest { values }
    }

    /**
     * Handles tab completion for the command.
     *
     * @param context The [CommandContext] representing the sender and arguments.
     * @return A list of suggestions for tab completion.
     */
    internal fun tabComplete(context: CommandContext): List<String> {
        if (!suggestionsEnabled) return emptyList()
        return suggestions?.invoke(context) ?: subCommands.keys.toList()
    }

    /**
     * Finds the appropriate executor for the provided arguments.
     *
     * @param args The list of arguments passed to the command.
     * @return A pair consisting of the [CommandBuilder] and remaining arguments.
     */
    internal fun findExecutor(args: List<String>): Pair<CommandBuilder, List<String>> {
        if (args.isEmpty()) return this to emptyList()
        val next = subCommands[args[0].lowercase()]
        return next?.findExecutor(args.drop(1)) ?: (this to args)
    }

    /**
     * Executes the command based on the [CommandContext].
     *
     * @param context The [CommandContext] containing the sender and arguments.
     */
    internal fun execute(context: CommandContext) {
        if (permission != null && !context.sender.hasPermission(permission!!)) {
            context.sender.sendMessage(MessageColor.color(permissionMessage))
            return
        }
        cooldownMillis?.let { delay ->
            val key = cooldownKey?.invoke(context) ?: context.sender.name
            if (CommandCooldowns.isOnCooldown(key, delay)) {
                context.sender.sendMessage(MessageColor.color("&cYou're on cooldown! Please wait ${delay / 1000} seconds."))
                return
            }
            CommandCooldowns.putCooldown(key)
        }
        try {
            validation?.invoke(context)
            executeBlock(context)
        } catch (exception: Exception) {
            context.sender.sendMessage(MessageColor.color("&c${exception.message ?: "Command failed"}"))
        }
    }
}