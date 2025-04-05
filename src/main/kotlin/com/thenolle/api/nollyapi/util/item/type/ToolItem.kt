package com.thenolle.api.nollyapi.util.item.type

import com.thenolle.api.nollyapi.util.item.CustomItem
import com.thenolle.api.nollyapi.util.item.CustomItems
import com.thenolle.api.nollyapi.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.meta.Damageable

/**
 * Represents a custom tool item with configurable attributes like attack damage, attack speed, and durability.
 *
 * This class allows creating custom tool items such as pickaxes, axes, shovels, etc., with the ability to modify
 * their attack damage, attack speed, and durability. The item can also be marked as unbreakable and given special
 * attributes like glow effects and lore.
 *
 * @param name The name of the tool item.
 * @param material The material type of the tool item. Default is [Material.DIAMOND_PICKAXE].
 */
@Suppress("UnstableApiUsage")
class ToolItem(private val name: String, material: Material = Material.DIAMOND_PICKAXE) {
    private val builder = ItemBuilder(material).name(name)

    private var unbreakable: Boolean = false
    private var attackDamage: Double? = null
    private var attackSpeed: Double? = null
    private var durability: Int? = null

    /**
     * Marks the tool item as unbreakable.
     *
     * @return The [ToolItem] instance for method chaining.
     */
    fun unbreakable() = apply { this.unbreakable = true }

    /**
     * Sets the attack damage of the tool item.
     *
     * @param damage The amount of damage the tool deals.
     * @return The [ToolItem] instance for method chaining.
     */
    fun attackDamage(damage: Double) = apply { this.attackDamage = damage }

    /**
     * Sets the attack speed of the tool item.
     *
     * @param speed The attack speed of the tool (higher is faster).
     * @return The [ToolItem] instance for method chaining.
     */
    fun attackSpeed(speed: Double) = apply { this.attackSpeed = speed }

    /**
     * Sets the durability (damage) of the tool item.
     *
     * @param durability The durability (damage value) of the tool item.
     * @return The [ToolItem] instance for method chaining.
     */
    fun durability(durability: Int) = apply { this.durability = durability }

    /**
     * Adds lore (descriptive text) to the tool item.
     *
     * @param lines The lines of lore to add.
     * @return The [ToolItem] instance for method chaining.
     */
    fun lore(vararg lines: String) = apply { builder.lore(*lines) }

    /**
     * Adds a glowing effect to the tool item.
     *
     * @return The [ToolItem] instance for method chaining.
     */
    fun glow() = apply { builder.glow() }

    /**
     * Builds and returns the [CustomItem] representation of the tool item.
     *
     * @param id The unique ID for the custom tool item.
     * @return The created [CustomItem] instance with the specified ID.
     */
    fun build(id: String): CustomItem {
        val item = builder.build()
        val meta = item.itemMeta ?: return CustomItem(item, id)

        if (unbreakable) meta.isUnbreakable = true
        if (durability != null && meta is Damageable) meta.damage = durability!!

        if (attackDamage != null || attackSpeed != null) {
            if (attackDamage != null) {
                meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier(
                        NamespacedKey.minecraft("attack_damage"),
                        attackDamage!!,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HAND
                    )
                )
            }

            if (attackSpeed != null) {
                meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_SPEED, AttributeModifier(
                        NamespacedKey.minecraft("attack_speed"),
                        attackSpeed!!,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HAND
                    )
                )
            }
        }

        meta.let { item.itemMeta = it }

        val customItem = CustomItem(item, id)
        CustomItems.define(id) { customItem }
        return customItem
    }
}