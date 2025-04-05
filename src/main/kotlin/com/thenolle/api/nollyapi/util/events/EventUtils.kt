package com.thenolle.api.nollyapi.util.events

import org.bukkit.Bukkit
import org.bukkit.event.Event

/**
 * Utility function to fire an event.
 *
 * This function calls the [Bukkit.getPluginManager().callEvent] method to trigger the specified event and notify
 * any listeners that are registered for that event.
 *
 * @param event The event to fire.
 * @return The same event object that was passed as an argument, allowing for method chaining if needed.
 */
fun <T: Event> fireEvent(event: T): T {
    Bukkit.getPluginManager().callEvent(event)
    return event
}