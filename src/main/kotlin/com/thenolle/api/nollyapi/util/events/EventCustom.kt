package com.thenolle.api.nollyapi.util.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * A custom event class that extends [Event].
 *
 * This class allows the addition of metadata to the event and provides utility methods to set, get, check,
 * and remove metadata. It is meant to be subclassed for specific custom event implementations.
 */
abstract class EventCustom : Event() {
    companion object {
        /** A static instance of [HandlerList] used by this event. */
        @JvmStatic
        val HANDLERS = HandlerList()
    }

    /**
     * Returns the [HandlerList] associated with this event.
     *
     * @return The [HandlerList] for this event.
     */
    override fun getHandlers(): HandlerList = HANDLERS

    /** A map to store metadata associated with this event. */
    private val metadata: MutableMap<String, Any> = mutableMapOf()

    /**
     * Retrieves the value of the metadata for a specific key.
     *
     * @param key The key of the metadata to retrieve.
     * @return The value of the metadata, or null if the key does not exist.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getMeta(key: String): T? = metadata[key] as? T

    /**
     * Sets a value for the metadata with a specific key.
     *
     * @param key The key of the metadata.
     * @param value The value to set for the metadata.
     * @return The [EventCustom] instance with the updated metadata.
     */
    fun <T> setMeta(key: String, value: T): EventCustom = apply { metadata[key] = value as Any }

    /**
     * Checks if a metadata key exists.
     *
     * @param key The key of the metadata.
     * @return True if the metadata exists, otherwise false.
     */
    fun hasMeta(key: String): Boolean = metadata.containsKey(key)

    /**
     * Removes the metadata associated with a specific key.
     *
     * @param key The key of the metadata to remove.
     * @return The [EventCustom] instance with the updated metadata.
     */
    fun removeMeta(key: String): EventCustom = apply { metadata.remove(key) }

    /**
     * Returns the set of all metadata keys.
     *
     * @return A set containing all the metadata keys.
     */
    fun metaKeys(): Set<String> = metadata.keys
}