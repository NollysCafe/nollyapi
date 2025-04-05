package com.thenolle.api.nollyapi.util.item.type

import com.thenolle.api.nollyapi.util.item.CustomItem
import com.thenolle.api.nollyapi.util.item.CustomItems
import com.thenolle.api.nollyapi.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.Damageable

/**
 * Represents a custom armor item with customizable attributes and properties.
 *
 * This class allows the creation of armor items with specific attributes such as armor points,
 * toughness, knockback resistance, and durability. It also supports setting item properties like
 * unbreakability, glow effects, and lore.
 *
 * The created armor item can be used as a [CustomItem] which can be registered and given to players.
 *
 * @param name The name of the armor item.
 * @param material The [Material] of the armor item (e.g., DIAMOND_HELMET, IRON_CHESTPLATE).
 */
@Suppress("UnstableApiUsage")
class ArmorItem(private val name: String, material: Material) {
    private val builder = ItemBuilder(material).name(name)

    // Armor properties
    private var unbreakable: Boolean = false
    private var durability: Int? = null
    private var armorPoints: Double? = null
    private var armorToughness: Double? = null
    private var knockbackResistance: Double? = null
    private var slot: EquipmentSlotGroup = resolveSlot(material)

    /**
     * Sets whether the armor item is unbreakable.
     *
     * @param flag A boolean flag indicating if the armor should be unbreakable.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun unbreakable(flag: Boolean = true): ArmorItem = apply { this.unbreakable = flag }

    /**
     * Sets the durability of the armor item.
     *
     * @param amount The durability amount of the armor item.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun durability(amount: Int) = apply { this.durability = amount }

    /**
     * Sets the armor points of the armor item.
     *
     * @param points The number of armor points to be added.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun armor(points: Double) = apply { this.armorPoints = points }

    /**
     * Sets the armor toughness of the armor item.
     *
     * @param points The number of armor toughness points to be added.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun toughness(points: Double) = apply { this.armorToughness = points }

    /**
     * Sets the knockback resistance of the armor item.
     *
     * @param amount The amount of knockback resistance to be added.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun knockbackResistance(amount: Double) = apply { this.knockbackResistance = amount }

    /**
     * Sets the equipment slot of the armor item (HEAD, CHEST, LEGS, FEET).
     *
     * @param slot The [EquipmentSlotGroup] representing the armor's equipment slot.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun slot(slot: EquipmentSlotGroup) = apply { this.slot = slot }

    /**
     * Adds a glowing effect to the armor item.
     *
     * @return The [ArmorItem] instance for method chaining.
     */
    fun glow() = apply { builder.glow() }

    /**
     * Adds lore to the armor item.
     *
     * @param lines The lines of lore to add to the item.
     * @return The [ArmorItem] instance for method chaining.
     */
    fun lore(vararg lines: String) = apply { builder.lore(*lines) }

    // Determines the equipment slot based on the material's name
    private fun resolveSlot(material: Material): EquipmentSlotGroup = when {
        material.name.contains("HELMET", ignoreCase = true) -> EquipmentSlotGroup.HEAD
        material.name.contains("CHESTPLATE", ignoreCase = true) -> EquipmentSlotGroup.CHEST
        material.name.contains("LEGGINGS", ignoreCase = true) -> EquipmentSlotGroup.LEGS
        material.name.contains("BOOTS", ignoreCase = true) -> EquipmentSlotGroup.FEET
        else -> EquipmentSlotGroup.HAND
    }

    /**
     * Builds and returns the [CustomItem] representation of the armor item.
     *
     * @param id The unique ID for the custom item.
     * @return The created [CustomItem] instance with the specified ID.
     */
    fun build(id: String): CustomItem {
        val item = builder.build()
        val meta = item.itemMeta as? ArmorMeta ?: return CustomItem(item, id)

        if (unbreakable) meta.isUnbreakable = true
        if (durability != null && meta is Damageable) meta.damage = durability!!

        if (armorPoints != null) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR, AttributeModifier(
                    NamespacedKey.minecraft("armor_points"), armorPoints!!, AttributeModifier.Operation.ADD_NUMBER, slot
                )
            )
        }

        if (armorToughness != null) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier(
                    NamespacedKey.minecraft("armor_toughness"),
                    armorToughness!!,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot
                )
            )
        }

        if (knockbackResistance != null) {
            meta.addAttributeModifier(
                Attribute.GENERIC_KNOCKBACK_RESISTANCE, AttributeModifier(
                    NamespacedKey.minecraft("knockback_resistance"),
                    knockbackResistance!!,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot
                )
            )
        }

        meta.let { item.itemMeta = it }

        val customItem = CustomItem(item, id)
        CustomItems.define(id) { customItem }
        return customItem
    }
}