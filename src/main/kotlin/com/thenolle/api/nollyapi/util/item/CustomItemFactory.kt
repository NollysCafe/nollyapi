package com.thenolle.api.nollyapi.util.item

/**
 * Factory object for managing custom items. It allows registering, creating, and retrieving custom items by their IDs.
 *
 * This factory ensures that custom items are unique and provides access to all registered items.
 */
object CustomItemFactory {
    // A map that holds all registered custom item definitions
    private val definitions = mutableMapOf<String, CustomItemDefinition>()

    /**
     * Registers a new custom item by its ID and builder function.
     *
     * @param id The unique identifier for the custom item.
     * @param builder The builder function that creates the custom item.
     * @throws IllegalArgumentException If the ID already exists in the registry.
     */
    fun register(id: String, builder: () -> CustomItem) {
        // Check for duplicate IDs and throw an exception if the ID already exists
        require(!definitions.containsKey(id)) { "Duplicate custom item ID: $id" }
        definitions[id] = CustomItemDefinition(id, builder)
    }

    /**
     * Creates a custom item based on its ID.
     *
     * @param id The unique identifier for the custom item.
     * @return A new instance of the custom item or `null` if the item is not registered.
     */
    fun create(id: String): CustomItem? = definitions[id]?.builder?.invoke()

    /**
     * Retrieves all registered custom item definitions.
     *
     * @return A map containing all the registered custom item definitions, with their IDs as keys.
     */
    fun all(): Map<String, CustomItemDefinition> = definitions.toMap()
}