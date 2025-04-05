package com.thenolle.api.nollyapi.util.item.type

import com.thenolle.api.nollyapi.util.item.CustomItem
import com.thenolle.api.nollyapi.util.item.CustomItems
import com.thenolle.api.nollyapi.util.item.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

/**
 * Represents a custom food item with customizable properties and effects.
 *
 * This class allows the creation of food items that can restore hunger and saturation, apply potion effects
 * upon consumption, and have additional features like the ability to always be eaten or conversion into another item.
 * The created food item can be used as a [CustomItem], which can be registered and given to players.
 *
 * @param name The name of the food item.
 * @param material The [Material] of the food item (default is COOKED_BEEF).
 */
@Suppress("UnstableApiUsage")
class FoodItem(private val name: String, material: Material = Material.COOKED_BEEF) {
    private val builder = ItemBuilder(material).name(name)

    // Food properties
    private var hunger: Int = 0
    private var saturation: Float = 0f
    private var canAlwaysEat: Boolean = false
    private var eatSeconds: Float = 1.6f
    private var effects: MutableList<Pair<PotionEffect, Float>> = mutableListOf<Pair<PotionEffect, Float>>()
    private var eatingConvertsTo: ItemStack? = null

    /**
     * Sets the number of hunger points the food item restores.
     *
     * @param points The number of hunger points.
     * @return The [FoodItem] instance for method chaining.
     */
    fun hunger(points: Int) = apply { this.hunger = points }

    /**
     * Sets the saturation the food item provides.
     *
     * @param amount The amount of saturation.
     * @return The [FoodItem] instance for method chaining.
     */
    fun saturation(amount: Float) = apply { this.saturation = amount }

    /**
     * Makes the food item always consumable, even when the player is not hungry.
     *
     * @return The [FoodItem] instance for method chaining.
     */
    fun canAlwaysEat() = apply { this.canAlwaysEat = true }

    /**
     * Sets the time (in seconds) it takes to eat the food item.
     *
     * @param seconds The time it takes to eat in seconds.
     * @return The [FoodItem] instance for method chaining.
     */
    fun eatSeconds(seconds: Float) = apply { this.eatSeconds = seconds }

    /**
     * Adds a potion effect to the food item, which will be applied with a certain probability upon consumption.
     *
     * @param effect The [PotionEffect] to apply.
     * @param probability The probability (between 0 and 1) of applying the effect.
     * @return The [FoodItem] instance for method chaining.
     */
    fun addEffect(effect: PotionEffect, probability: Float) = apply { effects.add(effect to probability) }

    /**
     * Sets the item that the food will convert to once it is eaten.
     *
     * @param item The [ItemStack] the food will convert to after consumption.
     * @return The [FoodItem] instance for method chaining.
     */
    fun eatingConvertsTo(item: ItemStack) = apply { this.eatingConvertsTo = item }

    /**
     * Adds lore to the food item.
     *
     * @param lines The lines of lore to add.
     * @return The [FoodItem] instance for method chaining.
     */
    fun lore(vararg lines: String) = apply { builder.lore(*lines) }

    /**
     * Adds a glowing effect to the food item.
     *
     * @return The [FoodItem] instance for method chaining.
     */
    fun glow() = apply { builder.glow() }

    /**
     * Marks the food item as unbreakable.
     *
     * @return The [FoodItem] instance for method chaining.
     */
    fun unbreakable() = apply { builder.unbreakable() }

    /**
     * Builds and returns the [CustomItem] representation of the food item.
     *
     * @param id The unique ID for the custom item.
     * @return The created [CustomItem] instance with the specified ID.
     */
    fun build(id: String): CustomItem {
        val item = builder.build()
        val itemFactory = Bukkit.getItemFactory()
        val meta = item.itemMeta ?: return CustomItem(item, id)

        val foodComponent = itemFactory.createItemStack("minecraft:apple").itemMeta?.food
            ?: throw IllegalStateException("Could not create FoodComponent")

        foodComponent.nutrition = this.hunger
        foodComponent.saturation = this.saturation
        foodComponent.setCanAlwaysEat(this.canAlwaysEat)
        foodComponent.eatSeconds = eatSeconds
        if (eatingConvertsTo != null) foodComponent.usingConvertsTo = eatingConvertsTo
        for ((effect, probability) in effects) foodComponent.addEffect(effect, probability)

        meta.setFood(foodComponent)

        meta.let { item.itemMeta = it }

        val customItem = CustomItem(item, id)
        CustomItems.define(id) { customItem }
        return customItem
    }
}