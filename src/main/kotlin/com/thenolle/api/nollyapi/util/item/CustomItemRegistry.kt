package com.thenolle.api.nollyapi.util.item

import org.bukkit.inventory.ItemStack

/**
 * A registry for managing custom items by their IDs.
 *
 * This object is responsible for storing and retrieving custom items based on their unique IDs.
 * The registry allows custom items to be accessed and manipulated through their IDs, and it provides
 * functionality to register items and find items from an `ItemStack` based on metadata.
 */
object CustomItemRegistry {
    // A mutable map to hold registered custom items, indexed by their unique IDs
    private val items: MutableMap<String, CustomItem> = mutableMapOf()

    /**
     * Registers a custom item in the registry with its unique ID.
     *
     * This function allows a custom item to be added to the registry, where it can later be found
     * and retrieved by its ID.
     *
     * @param id The unique identifier for the custom item.
     * @param item The custom item to be registered.
     */
    fun register(id: String, item: CustomItem) = apply { items[id] = item }

    /**
     * Finds a custom item from an `ItemStack` based on its metadata.
     *
     * This function extracts the custom item ID from the lore of the provided `ItemStack` and retrieves
     * the corresponding custom item from the registry.
     *
     * @param item The `ItemStack` representing the custom item.
     * @return The custom item associated with the given `ItemStack`, or `null` if not found.
     */
    fun findByItem(item: ItemStack?): CustomItem? {
        // Extract the custom item ID from the lore of the item
        val id = item?.itemMeta?.lore?.find { it.contains("[custom:") }
            ?.substringAfter("[custom:")?.substringBefore("]") ?: return null

        // Return the custom item from the registry by its ID
        return items[id]
    }
}