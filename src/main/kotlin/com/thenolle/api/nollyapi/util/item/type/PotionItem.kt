package com.thenolle.api.nollyapi.util.item.type

import com.thenolle.api.nollyapi.util.item.CustomItem
import com.thenolle.api.nollyapi.util.item.CustomItems
import com.thenolle.api.nollyapi.util.item.ItemBuilder
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

/**
 * Represents a custom potion item with customizable effects, color, and type.
 *
 * This class allows the creation of potions with different effects, colors, and types (regular, splash, or lingering).
 * The potion can also be customized with various lore, glow effects, and unbreakable properties.
 * The created potion item can be used as a [CustomItem] and can be registered and given to players.
 *
 * @param name The name of the potion item.
 * @param base The base potion type (default is [PotionType.WATER]).
 * @param splash Whether the potion should be a splash potion (default is `false`).
 * @param lingering Whether the potion should be a lingering potion (default is `false`).
 */
class PotionItem(
    private val name: String,
    private val base: PotionType = PotionType.WATER,
    private val splash: Boolean = false,
    private val lingering: Boolean = false,
) {
    private val builder = ItemBuilder(resolveMaterial()).name(name)

    private val effects: MutableList<PotionEffect> = mutableListOf()
    private var overrideColor: Color? = null

    /**
     * Adds a [PotionEffect] to the potion.
     *
     * @param type The type of potion effect to apply.
     * @param duration The duration of the effect in ticks.
     * @param amplifier The amplifier level of the effect.
     * @param ambient Whether the effect is ambient (default is `false`).
     * @return The [PotionItem] instance for method chaining.
     */
    fun effect(type: PotionEffectType, duration: Int, amplifier: Int, ambient: Boolean = false): PotionItem =
        apply { effects += PotionEffect(type, duration, amplifier, ambient, true) }

    /**
     * Sets the color of the potion.
     *
     * @param r The red component of the color.
     * @param g The green component of the color.
     * @param b The blue component of the color.
     * @return The [PotionItem] instance for method chaining.
     */
    fun color(r: Int, g: Int, b: Int): PotionItem = apply { overrideColor = Color.fromRGB(r, g, b) }

    /**
     * Sets the color of the potion.
     *
     * @param color The [Color] of the potion.
     * @return The [PotionItem] instance for method chaining.
     */
    fun color(color: Color) = apply { overrideColor = color }

    /**
     * Adds lore to the potion item.
     *
     * @param lines The lines of lore to add.
     * @return The [PotionItem] instance for method chaining.
     */
    fun lore(vararg lines: String) = apply { builder.lore(*lines) }

    /**
     * Adds a glowing effect to the potion item.
     *
     * @return The [PotionItem] instance for method chaining.
     */
    fun glow() = apply { builder.glow() }

    /**
     * Marks the potion item as unbreakable.
     *
     * @return The [PotionItem] instance for method chaining.
     */
    fun unbreakable() = apply { builder.unbreakable() }

    /**
     * Resolves the appropriate [Material] based on the potion type (regular, splash, or lingering).
     *
     * @return The [Material] corresponding to the potion type.
     */
    private fun resolveMaterial(): Material = when {
        lingering -> Material.LINGERING_POTION
        splash -> Material.SPLASH_POTION
        else -> Material.POTION
    }

    /**
     * Builds and returns the [CustomItem] representation of the potion item.
     *
     * @param id The unique ID for the custom item.
     * @return The created [CustomItem] instance with the specified ID.
     */
    fun build(id: String): CustomItem {
        val item = builder.build()
        val meta = item.itemMeta as? PotionMeta ?: return CustomItem(item, id)

        meta.basePotionType = base
        effects.forEach { meta.addCustomEffect(it, true) }
        if (overrideColor != null) meta.color = overrideColor

        meta.let { item.itemMeta = it }

        val customItem = CustomItem(item, id)
        CustomItems.define(id) { customItem }
        return customItem
    }
}