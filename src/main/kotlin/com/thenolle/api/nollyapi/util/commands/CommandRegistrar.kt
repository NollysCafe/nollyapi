package com.thenolle.api.nollyapi.util.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import java.lang.reflect.Field

/**
 * Registers commands with the Bukkit server's command map.
 *
 * This object is responsible for registering commands and their associated behaviors, such as execution
 * logic and tab completion, with the server's command map.
 */
object CommandRegistrar {
    private val commandMap: CommandMap by lazy {
        val field: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        field.get(Bukkit.getServer()) as CommandMap
    }

    /**
     * Registers a [CommandBuilder] as a Bukkit command.
     *
     * This method creates a [BukkitCommand] using the details from the [CommandBuilder] and registers it
     * with the command map.
     *
     * @param builder The [CommandBuilder] used to define the command's behavior and properties.
     */
    fun register(builder: CommandBuilder) {
        val command = object : BukkitCommand(builder.name) {
            override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
                try {
                    val (executor, remaining) = builder.findExecutor(args.toList())
                    executor.execute(CommandContext(sender, label, remaining))
                } catch (ex: Exception) {
                    sender.sendMessage(ex.message ?: "Unknown command error.")
                }
                return true
            }

            override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
                return try {
                    val context = CommandContext(sender, alias, args.toList())
                    val (executor, remaining) = builder.findExecutor(args.toList())
                    executor.tabComplete(context.copy(args = remaining))
                } catch (ex: Exception) {
                    emptyList()
                }
            }
        }

        command.description = builder.description
        command.permission = builder.permission
        command.usage = builder.usage
        builder.aliases.forEach { commandMap.register(it, command) }
        commandMap.register("nollyapi", command)
    }
}
