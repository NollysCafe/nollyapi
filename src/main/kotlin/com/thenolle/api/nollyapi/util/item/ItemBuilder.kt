package com.thenolle.api.nollyapi.util.item

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * A builder class for creating and customizing [ItemStack] objects in a flexible manner.
 * Allows setting properties like display name, lore, enchantments, flags, and more.
 *
 * @param material The material of the item being built.
 */
class ItemBuilder(material: Material) {
    private val item: ItemStack = ItemStack(material)
    private val meta: ItemMeta? get() = item.itemMeta

    /**
     * Sets the display name of the item.
     *
     * @param name The name to set for the item.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun name(name: String): ItemBuilder {
        meta?.apply {
            setDisplayName(name.color())
            applyTo(item)
        }
        return this
    }

    /**
     * Sets the lore (description) of the item.
     *
     * @param lines The lines of lore to set.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun lore(vararg lines: String): ItemBuilder {
        meta?.apply {
            lore = lines.map { it.color() }
            applyTo(item)
        }
        return this
    }

    /**
     * Adds a glow effect to the item.
     *
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun glow(): ItemBuilder {
        meta?.apply {
            setEnchantmentGlintOverride(true)
            applyTo(item)
        }
        return this
    }

    /**
     * Makes the item unbreakable.
     *
     * @param flag Whether to make the item unbreakable or not.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun unbreakable(flag: Boolean = true): ItemBuilder {
        meta?.apply {
            isUnbreakable = flag
            applyTo(item)
        }
        return this
    }

    /**
     * Sets the flags for the item.
     *
     * @param flags The [ItemFlag] values to add to the item.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun flags(vararg flags: ItemFlag): ItemBuilder {
        meta?.apply {
            addItemFlags(*flags)
            applyTo(item)
        }
        return this
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment The enchantment to add.
     * @param level The level of the enchantment.
     * @param ignoreLimit Whether to ignore the enchantment's level limit.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun enchant(enchantment: Enchantment, level: Int, ignoreLimit: Boolean = false): ItemBuilder {
        meta?.apply {
            addEnchant(enchantment, level, ignoreLimit)
            applyTo(item)
        }
        return this
    }

    /**
     * Sets a custom model data ID for the item.
     *
     * @param id The custom model data ID to set.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun customModelData(id: Int): ItemBuilder {
        meta?.apply {
            setCustomModelData(id)
            applyTo(item)
        }
        return this
    }

    /**
     * Sets the amount (stack size) of the item.
     *
     * @param amount The number of items in the stack.
     * @return The [ItemBuilder] instance for method chaining.
     */
    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    /**
     * Builds and returns the final [ItemStack] with all applied modifications.
     *
     * @return The customized [ItemStack] instance.
     */
    fun build(): ItemStack {
        meta?.let { item.itemMeta = it }
        return item
    }

    private fun ItemMeta.applyTo(item: ItemStack) = apply { item.itemMeta = this }
}