package com.thenolle.api.nollyapi.util.item.type

import com.thenolle.api.nollyapi.util.item.CustomItem
import com.thenolle.api.nollyapi.util.item.CustomItems
import com.thenolle.api.nollyapi.util.item.ItemBuilder
import org.bukkit.Material

/**
 * Represents a simple custom item with basic attributes like name, material, lore, and special effects.
 *
 * This class provides an easy way to create basic items in Minecraft with custom names, lore, glow effects,
 * unbreakable properties, custom model data, and item amounts. The created item can be registered as a
 * [CustomItem] and given to players.
 *
 * @param name The name of the item.
 * @param material The material type of the item.
 */
class SimpleItem(private val name: String, private val material: Material) {
    private val builder = ItemBuilder(material).name(name)

    /**
     * Adds lore (descriptive text) to the item.
     *
     * @param lines The lines of lore to add.
     * @return The [SimpleItem] instance for method chaining.
     */
    fun lore(vararg lines: String) = apply { builder.lore(*lines) }

    /**
     * Adds a glowing effect to the item.
     *
     * @return The [SimpleItem] instance for method chaining.
     */
    fun glow() = apply { builder.glow() }

    /**
     * Marks the item as unbreakable.
     *
     * @return The [SimpleItem] instance for method chaining.
     */
    fun unbreakable() = apply { builder.unbreakable() }

    /**
     * Sets the custom model data for the item.
     *
     * @param data The custom model data value to apply.
     * @return The [SimpleItem] instance for method chaining.
     */
    fun customModelData(data: Int) = apply { builder.customModelData(data) }

    /**
     * Sets the amount (stack size) of the item.
     *
     * @param amount The amount to set for the item stack.
     * @return The [SimpleItem] instance for method chaining.
     */
    fun amount(amount: Int) = apply { builder.amount(amount) }

    /**
     * Builds and returns the [CustomItem] representation of the item.
     *
     * @param id The unique ID for the custom item.
     * @return The created [CustomItem] instance with the specified ID.
     */
    fun build(id: String): CustomItem {
        val item = builder.build()
        val customItem = CustomItem(item, id)
        CustomItems.define(id) { customItem }
        return customItem
    }
}