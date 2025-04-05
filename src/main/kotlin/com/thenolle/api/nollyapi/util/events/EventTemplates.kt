package com.thenolle.api.nollyapi.util.events

import org.bukkit.event.Event

/**
 * A singleton object that manages event templates.
 *
 * This object allows you to register and retrieve event templates that can be used to define common event filters
 * or behaviors for various event types. Templates are stored by a unique name and can be reused across different
 * event scopes.
 */
object EventTemplates {
    /** A map of template names to the event scope configurations. */
    private val templates = mutableMapOf<String, EventScope<out Event>.() -> Unit>()

    /**
     * Registers a new event template with the specified name.
     *
     * The template is a function that configures an [EventScope] for a specific event type. It can be reused
     * to apply the same conditions or filters across multiple event listeners.
     *
     * @param name The name of the event template to register.
     * @param template The event scope configuration function to register.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> register(name: String, template: EventScope<T>.() -> Unit) {
        templates[name] = template as EventScope<*>.() -> Unit
    }

    /**
     * Retrieves an event template by name.
     *
     * @param name The name of the event template to retrieve.
     * @return The event scope configuration function for the template, or null if no template with the specified
     * name exists.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> get(name: String): (EventScope<T>.() -> Unit)? {
        return templates[name] as? EventScope<T>.() -> Unit
    }
}