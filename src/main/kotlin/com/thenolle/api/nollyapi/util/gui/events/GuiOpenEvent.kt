package com.thenolle.api.nollyapi.util.gui.events

import com.thenolle.api.nollyapi.util.events.EventCustom
import com.thenolle.api.nollyapi.util.gui.GuiBuilder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Represents an event triggered when a GUI is opened for a player.
 *
 * This event is fired when a player opens a GUI. It contains information about the player and the
 * [GuiBuilder] that represents the opened GUI.
 *
 * @param player The player who opened the GUI.
 * @param gui The [GuiBuilder] that represents the GUI being opened.
 */
class GuiOpenEvent(val player: Player, val gui: GuiBuilder) : EventCustom() {
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