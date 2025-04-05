package com.thenolle.api.nollyapi.util.events

/**
 * A group that holds multiple [EventHandle]s.
 *
 * This class provides functionality to add event handlers to the group and unregister them all at once.
 */
class EventGroup {
    private val handles = mutableListOf<EventHandle>()

    /**
     * Adds an [EventHandle] to the group.
     *
     * @param handle The [EventHandle] to add to the group.
     */
    fun add(handle: EventHandle) = apply { handles += handle }

    /**
     * Unregisters all event handlers in the group.
     */
    fun unlistenAll() {
        handles.forEach { it.unlisten() }
        handles.clear()
    }
}

/**
 * Creates an [EventGroup] and applies the provided block to configure it.
 *
 * @param block The block used to configure the [EventGroup].
 * @return A configured [EventGroup].
 */
fun group(block: EventGroup.() -> Unit): EventGroup = EventGroup().apply(block)