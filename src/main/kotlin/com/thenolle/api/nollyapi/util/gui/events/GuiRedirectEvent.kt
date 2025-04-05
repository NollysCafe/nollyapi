package com.thenolle.api.nollyapi.util.gui.events

import com.thenolle.api.nollyapi.util.events.EventCustom
import com.thenolle.api.nollyapi.util.gui.GuiBuilder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Represents an event triggered when a player is redirected from one GUI to another.
 *
 * This event is fired when a player is redirected from one GUI to another. It contains information about the
 * player, the GUI they are coming from, and the target GUI they are being redirected to.
 *
 * @param player The player who is being redirected.
 * @param from The [GuiBuilder] representing the GUI the player is coming from.
 * @param to The [GuiBuilder] representing the GUI the player is being redirected to.
 */
class GuiRedirectEvent(val player: Player, val from: GuiBuilder, val to: GuiBuilder) : EventCustom() {
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