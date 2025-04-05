package com.thenolle.api.nollyapi.util.gui.events

import com.thenolle.api.nollyapi.util.events.EventCustom
import com.thenolle.api.nollyapi.util.gui.core.GuiButton
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * Represents an event triggered when a button within a GUI is clicked by a player.
 *
 * This event is fired when a player clicks a button in the GUI. It contains information about the player who
 * clicked, the slot where the button was located, the original [InventoryClickEvent], and the [GuiButton] that
 * was clicked.
 *
 * @param player The player who clicked the button.
 * @param slot The slot in the inventory where the button is located.
 * @param event The original [InventoryClickEvent] that triggered this event.
 * @param button The [GuiButton] that was clicked by the player.
 */
class GuiClickEvent(
    val player: Player, val slot: Int, val event: InventoryClickEvent, val button: GuiButton
) : EventCustom() {
    companion object {
        /** The handler list for this event type. */
        @JvmStatic
        val HANDLERS = HandlerList()

        /**
         * Returns the handler list for this event type.
         *
         * @return The handler list for this event.
         */
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList = HANDLERS
}