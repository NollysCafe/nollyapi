package com.thenolle.api.nollyapi.util.gui.events

import com.thenolle.api.nollyapi.util.events.EventCustom
import com.thenolle.api.nollyapi.util.gui.GuiBuilder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * Represents an event triggered when a GUI is closed by a player.
 *
 * This event is fired when a player closes the GUI. It contains information about the player who closed the
 * GUI, the GUI itself, and the original [InventoryCloseEvent].
 *
 * @param player The player who closed the GUI.
 * @param gui The [GuiBuilder] that represents the GUI being closed.
 * @param event The original [InventoryCloseEvent] that triggered this event.
 */
class GuiCloseEvent(val player: Player, val gui: GuiBuilder, val event: InventoryCloseEvent) : EventCustom() {
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