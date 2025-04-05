package com.thenolle.api.nollyapi.util.msg

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * A builder class for creating and sending messages with various types, including chat, action bars, titles, and boss bars.
 *
 * This class allows for building complex messages with conditional filters, chat messages, success/error messages,
 * action bar messages, titles, and even broadcasting messages to all players. You can customize the message's format,
 * apply color, and use placeholders to dynamically replace parts of the message.
 */
class MessageBuilder {
    var prefix: String = "&3&l[NollyAPI]&r"
    var target: CommandSender? = null

    private val components: MutableList<TextComponent> = mutableListOf()
    private var filters: MutableList<(Player) -> Boolean> = mutableListOf()

    /**
     * Adds a filter to only apply the message to players who meet the condition specified in the predicate.
     *
     * @param predicate The condition that the player must meet for the message to be sent.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun filter(predicate: (Player) -> Boolean): MessageBuilder = apply { filters.add(predicate) }

    /**
     * Adds a filter to only apply the message to players with the specified permission.
     *
     * @param permission The permission that the player must have for the message to be sent.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun hasPermission(permission: String) = filter { it.hasPermission(permission) }

    /**
     * Adds a filter to only apply the message to players who have all specified permissions.
     *
     * @param permissions The list of permissions that the player must have for the message to be sent.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun hasPermissions(vararg permissions: String) = filter { player -> permissions.all { player.hasPermission(it) } }

    /**
     * Adds a filter to only apply the message to a player with the specified name.
     *
     * @param name The name of the player.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun isNamed(name: String) = filter { it.name.equals(name, ignoreCase = true) }

    /**
     * Adds a filter to only apply the message to a player with the specified game mode.
     *
     * @param gamemode The name of the game mode to check.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun hasGamemode(gamemode: String) = filter { it.gameMode.name.equals(gamemode, ignoreCase = true) }

    /**
     * Adds a filter to only apply the message to a player with the specified game mode.
     *
     * @param gamemode The game mode to check.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun hasGamemode(gamemode: GameMode) = filter { it.gameMode == gamemode }

    /**
     * Adds a filter to only apply the message to a player in the specified world.
     *
     * @param world The name of the world to check.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun isInWorld(world: String) = filter { it.world.name.equals(world, ignoreCase = true) }

    /**
     * Adds a filter to only apply the message to a player in the specified world.
     *
     * @param world The world to check.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun isInWorld(world: World) = filter { it.world == world }

    /**
     * Sets the target of the message to a specific player.
     *
     * @param player The player who will receive the message.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun to(player: Player): MessageBuilder = apply { this.target = player }

    /**
     * Sets the target of the message to a specific sender (could be player or console).
     *
     * @param sender The sender who will receive the message.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun to(sender: CommandSender): MessageBuilder = apply { this.target = sender }

    /**
     * Sends a regular chat message to the target.
     *
     * @param message The message to send.
     */
    fun chat(message: String) = target?.sendMessage(MessageColor.color("$prefix $message"))

    /**
     * Sends an error message to the target.
     *
     * @param message The error message to send.
     */
    fun error(message: String) = chat("&c❌ $message")

    /**
     * Sends a success message to the target.
     *
     * @param message The success message to send.
     */
    fun success(message: String) = chat("&a✔ $message")

    /**
     * Sends an action bar message to the target player.
     *
     * @param message The message to send in the action bar.
     */
    fun actionbar(message: String) = (target as? Player)?.spigot()
        ?.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(MessageColor.color(message)))

    /**
     * Sends a title message to the target player.
     *
     * @param title The title text.
     * @param subtitle The subtitle text.
     * @param fadeIn The fade-in time.
     * @param stay The time to stay visible.
     * @param fadeOut The fade-out time.
     */
    fun title(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20) =
        (target as? Player)?.sendTitle(MessageColor.color(title), MessageColor.color(subtitle), fadeIn, stay, fadeOut)

    /**
     * Creates and returns a [BossBar] for the target player.
     *
     * @param message The message displayed on the boss bar.
     * @param color The color of the boss bar.
     * @param style The style of the boss bar.
     * @return The created [BossBar].
     */
    fun bossBar(message: String, color: BarColor = BarColor.BLUE, style: BarStyle = BarStyle.SOLID): BossBar {
        val bar = Bukkit.createBossBar(MessageColor.color(message), color, style)
        (target as? Player)?.let { bar.addPlayer(it) }
        return bar
    }

    /**
     * Replaces placeholders in the message with corresponding values from the map.
     *
     * @param message The message with placeholders.
     * @param map The map of placeholders and values to replace them with.
     * @return The message with replaced placeholders.
     */
    fun placeholder(message: String, map: Map<String, String>) =
        map.entries.fold(message) { acc, (k, v) -> acc.replace("%$k%", v, ignoreCase = true) }
            .let { MessageColor.color(it) }

    /**
     * Replaces placeholders in the message with corresponding values from the arguments.
     *
     * @param message The message with placeholders.
     * @param args The arguments to replace the placeholders with.
     * @return The message with replaced placeholders.
     */
    fun placeholder(message: String, vararg args: Any): String {
        var replaced = message
        args.forEachIndexed { i, v -> replaced = replaced.replace("%$i%", v.toString(), ignoreCase = true) }
        return MessageColor.color(replaced)
    }

    /**
     * Sends a broadcast message to all online players.
     *
     * @param message The message to broadcast.
     */
    fun broadcast(message: String) =
        Bukkit.getOnlinePlayers().forEach { it.sendMessage(MessageColor.color("$prefix $message")) }

    /**
     * Sends a broadcast error message to all online players.
     *
     * @param message The error message to broadcast.
     */
    fun broadcastError(message: String) = broadcast("&c❌ $message")

    /**
     * Sends a broadcast success message to all online players.
     *
     * @param message The success message to broadcast.
     */
    fun broadcastSuccess(message: String) = broadcast("&a✔ $message")

    /**
     * Sends a broadcast action bar message to all online players.
     *
     * @param message The message to broadcast in the action bar.
     */
    fun broadcastActionbar(message: String) = Bukkit.getOnlinePlayers().forEach {
        it.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(MessageColor.color(message)))
    }

    /**
     * Sends a broadcast title message to all online players.
     *
     * @param title The title text.
     * @param subtitle The subtitle text.
     * @param fadeIn The fade-in time.
     * @param stay The time to stay visible.
     * @param fadeOut The fade-out time.
     */
    fun broadcastTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20) =
        Bukkit.getOnlinePlayers().forEach {
            it.sendTitle(MessageColor.color(title), MessageColor.color(subtitle), fadeIn, stay, fadeOut)
        }

    /**
     * Sends a broadcast boss bar message to all online players.
     *
     * @param message The message displayed on the boss bar.
     * @param color The color of the boss bar.
     * @param style The style of the boss bar.
     * @return The created [BossBar].
     */
    fun broadcastBossBar(message: String, color: BarColor = BarColor.BLUE, style: BarStyle = BarStyle.SOLID): BossBar {
        val bar = Bukkit.createBossBar(MessageColor.color(message), color, style)
        Bukkit.getOnlinePlayers().forEach { bar.addPlayer(it) }
        return bar
    }

    /**
     * Adds a custom text to the message.
     *
     * @param text The text to add.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun add(text: String): MessageBuilder = apply { components.add(TextComponent(MessageColor.color(text))) }

    /**
     * Adds a custom [TextComponent] to the message.
     *
     * @param component The [TextComponent] to add.
     * @return The [MessageBuilder] instance for method chaining.
     */
    fun add(component: TextComponent): MessageBuilder = apply { components.add(component) }

    /**
     * Sends the constructed message to the target.
     */
    fun send() {
        val player = target as? Player ?: return
        val finalComponent = TextComponent()
        components.forEach { finalComponent.addExtra(it) }
        player.spigot().sendMessage(ChatMessageType.CHAT, finalComponent)
    }
}