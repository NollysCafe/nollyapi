package com.thenolle.api.nollyapi.util.commands

import java.util.concurrent.ConcurrentHashMap

/**
 * Manages command cooldowns for players or other command senders.
 *
 * This object stores cooldown timestamps for each command and checks whether a user is on cooldown
 * for a particular command. It uses a [ConcurrentHashMap] to store cooldown data, ensuring thread safety
 * for commands executed asynchronously.
 */
object CommandCooldowns {
    private val cooldowns = ConcurrentHashMap<String, Long>()

    /**
     * Checks if the given key is currently on cooldown.
     *
     * @param key The unique key associated with the cooldown (typically the player’s name).
     * @param delay The cooldown duration in milliseconds.
     * @return True if the key is still on cooldown, false otherwise.
     */
    fun isOnCooldown(key: String, delay: Long): Boolean {
        val current = System.currentTimeMillis()
        val lastUsed = cooldowns.getOrDefault(key, 0L)
        return (current - lastUsed) < delay
    }

    /**
     * Puts the given key on cooldown by recording the current time.
     *
     * @param key The unique key to associate with the cooldown.
     */
    fun putCooldown(key: String) = apply { cooldowns[key] = System.currentTimeMillis() }
}