package com.thenolle.api.nollyapi.util.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent

/**
 * Defines a scope for filtering events based on various criteria.
 *
 * This class provides a set of functions to filter events based on conditions like player names, permissions,
 * world names, metadata, and more. It allows building complex event filters.
 */
class EventScope<T : Event>(val filters: MutableList<(T) -> Boolean> = mutableListOf()) {
    /**
     * Evaluates whether the event passes all filters defined in the scope.
     *
     * @param event The event to evaluate.
     * @return True if the event passes all filters, otherwise false.
     */
    fun test(event: T): Boolean = filters.all { it(event) }


    // Scope
    /**
     * Adds a condition to the scope.
     *
     * @param block The condition to apply to the event.
     * @return The updated [EventScope] instance.
     */
    fun scope(block: (T) -> Boolean) = apply { filters += block }

    /**
     * Negates the condition defined in the provided [scopeFunction].
     *
     * @param scopeFunction The scope function to negate.
     * @return The updated [EventScope] instance.
     */
    fun negate(scopeFunction: EventScope<T>.() -> Unit) = scope { !EventScope<T>().apply(scopeFunction).test(it) }


    // Custom
    /**
     * Adds a custom condition to the scope.
     *
     * @param block The condition to apply to the event.
     * @return The updated [EventScope] instance.
     */
    fun custom(block: (T) -> Boolean) = scope(block)

    /**
     * Adds a condition that checks if the event has metadata for a given key.
     *
     * @param key The metadata key to check.
     * @return The updated [EventScope] instance.
     */
    fun meta(key: String) = scope { (it as? EventCustom)?.hasMeta(key) == true }

    /**
     * Adds a condition that checks if the metadata value for a given key matches a specified test.
     *
     * @param key The metadata key to check.
     * @param test The test function to apply to the metadata value.
     * @return The updated [EventScope] instance.
     */
    fun metaMatch(key: String, test: (Any?) -> Boolean) = scope { test((it as? EventCustom)?.getMeta<Any>(key)) }


    // World
    /**
     * Adds a condition that checks if the event occurred in the specified world.
     *
     * @param name The name of the world.
     * @return The updated [EventScope] instance.
     */
    fun world(name: String) = scope { (it as? PlayerEvent)?.player?.world?.name == name }

    /**
     * Adds a condition that checks if the event did not occur in the specified world.
     *
     * @param name The name of the world.
     * @return The updated [EventScope] instance.
     */
    fun notWorld(name: String) = negate { world(name) }


    // Player
    /**
     * Adds a condition that checks if the event was triggered by a player with the specified name.
     *
     * @param name The player's name.
     * @return The updated [EventScope] instance.
     */
    fun player(name: String) = scope { (it as? PlayerEvent)?.player?.name == name }

    /**
     * Adds a condition that checks if the event was triggered by a specific player.
     *
     * @param player The player to check.
     * @return The updated [EventScope] instance.
     */
    fun player(player: Player) = scope { (it as? PlayerEvent)?.player == player }

    /**
     * Adds a condition that checks if the event was triggered by any of the specified player names.
     *
     * @param names The player names to check.
     * @return The updated [EventScope] instance.
     */
    fun oneOfPlayers(vararg names: String) = scope { (it as? PlayerEvent)?.player?.name in names }

    /**
     * Adds a condition that checks if the event was triggered by any of the specified players.
     *
     * @param players The players to check.
     * @return The updated [EventScope] instance.
     */
    fun oneOfPlayers(vararg players: Player) = scope { (it as? PlayerEvent)?.player in players }


    // Permission
    /**
     * Adds a condition that checks if the player involved in the event has the specified permission.
     *
     * @param permission The permission to check.
     * @return The updated [EventScope] instance.
     */
    fun permission(permission: String) = scope { (it as? PlayerEvent)?.player?.hasPermission(permission) == true }

    /**
     * Adds a condition that checks if the player has all of the specified permissions.
     *
     * @param permissions The permissions to check.
     * @return The updated [EventScope] instance.
     */
    fun permissionsAll(vararg permissions: String) =
        scope { (it as? PlayerEvent)?.player?.let { p -> permissions.all(p::hasPermission) } == true }

    /**
     * Adds a condition that checks if the player has at least one of the specified permissions.
     *
     * @param permissions The permissions to check.
     * @return The updated [EventScope] instance.
     */
    fun permissionsOne(vararg permissions: String) =
        scope { (it as? PlayerEvent)?.player?.let { p -> permissions.any(p::hasPermission) } == true }


    // Op
    /**
     * Adds a condition that checks if the player is an operator.
     *
     * @return The updated [EventScope] instance.
     */
    fun op() = scope { (it as? PlayerEvent)?.player?.isOp == true }

    /**
     * Adds a condition that checks if the player is not an operator.
     *
     * @return The updated [EventScope] instance.
     */
    fun notOp() = negate { op() }


    // Templates
    /**
     * Applies an event template with the specified name.
     *
     * @param name The name of the template to use.
     * @return The updated [EventScope] instance.
     */
    fun useTemplate(name: String): EventScope<T> {
        val template = EventTemplates.get<T>(name) ?: error("No template found for name: $name")
        return apply(template)
    }


    // Pause
    /**
     * Adds a condition that checks if the event is paused based on a given key.
     *
     * @param key The key to check for pause status.
     * @return The updated [EventScope] instance.
     */
    fun paused(key: String) = scope { PauseManager.isPaused(key) }

    /**
     * Adds a condition that checks if the event is not paused based on a given key.
     *
     * @param key The key to check for pause status.
     * @return The updated [EventScope] instance.
     */
    fun notPaused(key: String) = negate { paused(key) }


    /**
     * Companion object for creating an instance of [EventScope].
     */
    companion object {
        /**
         * Creates an [EventScope] for the specified event type [T] using the provided builder block.
         *
         * @param builder The block to configure the [EventScope].
         * @return The created [EventScope] instance.
         */
        inline fun <reified T : Event> create(builder: EventScope<T>.() -> Unit): EventScope<T> {
            return EventScope<T>().apply(builder)
        }
    }
}