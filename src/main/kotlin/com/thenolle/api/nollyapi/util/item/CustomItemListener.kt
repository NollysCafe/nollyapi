package com.thenolle.api.nollyapi.util.item

import com.thenolle.api.nollyapi.util.events.listen
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * Listener object that listens for player interactions with custom items.
 * It ensures that the correct custom item is handled based on the player's interaction.
 *
 * The listener triggers the appropriate actions when the player interacts with custom items in their hand.
 */
object CustomItemListener {
    init {
        // Listen for PlayerInteractEvent to detect custom item interactions
        listen<PlayerInteractEvent> { event ->
            // Only process the interaction if it's with the main hand
            if (event.hand != EquipmentSlot.HAND) return@listen

            // Get the item the player is interacting with
            val item = event.item ?: return@listen

            // Find the custom item and handle the interaction
            CustomItemRegistry.findByItem(item)?.handleInteract(event.player, event)
        }
    }
}