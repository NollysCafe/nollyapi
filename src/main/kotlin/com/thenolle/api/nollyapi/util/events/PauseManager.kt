package com.thenolle.api.nollyapi.util.events

/**
 * A singleton object to manage pause states for different operations identified by a key.
 *
 * The [PauseManager] allows you to pause and resume operations using a unique key. It can be useful for cases where
 * certain tasks or event listeners should be temporarily paused, for example, during maintenance or other critical periods.
 */
object PauseManager {
    /** A set of keys representing paused operations. */
    private val paused = mutableSetOf<String>()

    /**
     * Pauses the operation associated with the given key.
     *
     * @param key The key identifying the operation to pause.
     */
    fun pause(key: String) = paused.add(key)

    /**
     * Resumes the operation associated with the given key.
     *
     * @param key The key identifying the operation to resume.
     */
    fun resume(key: String) = paused.remove(key)

    /**
     * Checks if the operation associated with the given key is paused.
     *
     * @param key The key identifying the operation to check.
     * @return True if the operation is paused, otherwise false.
     */
    fun isPaused(key: String) = paused.contains(key)
}