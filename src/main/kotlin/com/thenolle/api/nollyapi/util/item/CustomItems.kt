package com.thenolle.api.nollyapi.util.item

/**
 * A utility object to define and retrieve custom items.
 * Custom items are registered by their unique identifier (ID).
 */
object CustomItems {
    /**
     * Defines a custom item by registering it with a unique ID.
     *
     * @param id The unique identifier for the custom item.
     * @param builder A lambda that builds the custom item.
     */
    fun define(id: String, builder: () -> CustomItem) = CustomItemFactory.register(id, builder)

    /**
     * Retrieves a custom item by its unique ID.
     *
     * @param id The unique identifier for the custom item.
     * @return The custom item associated with the ID, or null if not found.
     */
    fun get(id: String): CustomItem? = CustomItemFactory.create(id)
}