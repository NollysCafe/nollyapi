package com.thenolle.api.nollyapi.util.gui

import com.thenolle.api.nollyapi.util.events.fireEvent
import com.thenolle.api.nollyapi.util.events.listen
import com.thenolle.api.nollyapi.util.gui.events.GuiCloseEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

/**
 * The [GuiRegistry] object serves as a registry for tracking and managing open GUI instances.
 *
 * It maintains a mapping of open [Inventory] objects to their corresponding [GuiBuilder] instances.
 */
object GuiRegistry {
    /** A mutable map that tracks open inventories and their associated [GuiBuilder] instances. */
    val openGuis: MutableMap<Inventory, Any> = mutableMapOf()

    /**
     * Registers a new inventory with its associated GUI instance.
     *
     * @param inventory The inventory being registered.
     * @param gui The [GuiBuilder] instance representing the GUI.
     * @return The current [GuiRegistry] instance for chaining.
     */
    fun register(inventory: Inventory, gui: Any) = apply { openGuis[inventory] = gui }

    /**
     * Retrieves the GUI instance associated with a given inventory.
     *
     * @param inventory The inventory whose associated GUI is being retrieved.
     * @return The [GuiBuilder] instance associated with the given inventory, or null if not found.
     */
    fun get(inventory: Inventory): Any? = openGuis[inventory]

    /**
     * Retrieves the GUI instance associated with a given inventory, casting it to a specific type.
     *
     * @param inventory The inventory whose associated GUI is being retrieved and casted.
     * @return The [GuiBuilder] instance associated with the given inventory, or null if not found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getAs(inventory: Inventory): T? = get(inventory) as? T
}

/**
 * The [GuiListener] object listens for GUI-related events, including inventory click and close events.
 *
 * It handles the events related to GUI interaction, such as processing button clicks and notifying when a GUI is closed.
 */
object GuiListener {
    init {
        // Listens for inventory click events and handles button clicks within the GUI.
        listen<InventoryClickEvent> { event ->
            // Retrieves the GUI associated with the clicked inventory.
            val gui = GuiRegistry.get(event.inventory) as? GuiBuilder ?: return@listen
            // Handles the button click event.
            gui.handleClick(event)
        }

        // Listens for inventory close events and fires the GuiCloseEvent.
        listen<InventoryCloseEvent> { event ->
            // Retrieves the GUI associated with the closed inventory.
            val gui = GuiRegistry.get(event.inventory) as? GuiBuilder ?: return@listen
            // Ensures the player who closed the inventory is valid.
            val player = event.player as? Player ?: return@listen
            // Fires the GuiCloseEvent to notify that the GUI was closed.
            fireEvent(GuiCloseEvent(player, gui, event))
            // Removes the closed inventory from the registry.
            GuiRegistry.openGuis.remove(event.inventory)
        }
    }
}