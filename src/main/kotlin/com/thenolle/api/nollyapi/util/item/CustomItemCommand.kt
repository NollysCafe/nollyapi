package com.thenolle.api.nollyapi.util.item

import com.thenolle.api.nollyapi.util.commands.command
import com.thenolle.api.nollyapi.util.msg.message
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Represents a command that allows giving a registered custom item to a player.
 *
 * This class is used to register a command that lets a player receive a custom item in-game, either for themselves or another player.
 * The command can suggest item IDs and player names, making it easier to use.
 *
 * @param commandName The name of the command to register. Defaults to "ngive" if not provided.
 * @param configBlock A block to configure the command's properties such as its name, description, and permission.
 */
class CustomItemCommand(private val commandName: String? = null, configBlock: CommandConfig.() -> Unit = {}) {
    /**
     * Configuration options for the custom item command.
     *
     * @property name The name of the command.
     * @property description A brief description of what the command does.
     * @property permission The permission required to use the command.
     * @property usage A string that shows how to use the command.
     */
    class CommandConfig {
        var name: String? = null
        var description: String = "Gives a registered custom item to a player"
        var permission: String? = null
        var usage: String = "/<command> <item_id> [player]"
    }

    init {
        val config = CommandConfig().apply(configBlock)

        // Registering the command using the given configuration
        command(config.name ?: commandName ?: "ngive") {
            this.description = config.description
            this.permission = config.permission
            this.usage = config.usage.replace("<command>", config.name ?: commandName ?: "ngive")

            // Suggest item IDs based on the arguments passed
            suggest {
                when (it.args.size) {
                    1 -> {
                        return@suggest CustomItemFactory.all().keys.toList()
                    }

                    2 -> {
                        return@suggest Bukkit.getOnlinePlayers().map { it.name }
                    }
                }
                emptyList()
            }

            // Logic to run the command
            run {
                val itemId = args.getOrNull(0)
                if (itemId == null) {
                    // Notify the sender if the usage is incorrect
                    message(sender) { error("&cUsage: /$commandName <item_id> [player]") }
                    return@run
                }

                // Retrieve the custom item by its ID
                val item = CustomItems.get(itemId)
                if (item == null) {
                    // Notify the sender if the item ID is unknown
                    message(sender) { error("&cUnknown custom item: $itemId") }
                    return@run
                }

                // Determine the target player
                val target: Player? = when {
                    args.size >= 2 -> Bukkit.getPlayer(args[1]) // Use the player name if provided
                    sender is Player -> sender // Use the sender if it's a player
                    else -> null
                }

                if (target == null) {
                    // Notify if the target player is not found
                    message(sender) { error("&cPlayer not found: ${args.getOrNull(1)}") }
                    return@run
                }

                // Give the item to the target player and send success message
                item.giveTo(target)
                message(sender) { success("&aGave $itemId to ${target.name}") }
            }
        }
    }
}