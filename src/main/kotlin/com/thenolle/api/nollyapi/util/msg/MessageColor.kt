package com.thenolle.api.nollyapi.util.msg

import org.bukkit.ChatColor

/**
 * Utility object for handling color-related functionalities for messages.
 *
 * This object provides methods to translate color codes and strip color codes from strings.
 */
object MessageColor {
    /**
     * Converts color codes in the text using '&' to the Minecraft-compatible format.
     *
     * This method will replace all occurrences of '&' followed by a character with the corresponding Minecraft color.
     * For example, "&cHello" will turn into red-colored text.
     *
     * @param text The string that may contain Minecraft color codes.
     * @return The string with colors applied.
     */
    fun color(text: String): String = ChatColor.translateAlternateColorCodes('&', text)

    /**
     * Strips color codes from the given string.
     *
     * This method removes all Minecraft color codes from the string, returning the plain text.
     * For example, "&cHello" will return "Hello".
     *
     * @param text The string with color codes to be stripped.
     * @return The plain text without any color codes.
     */
    fun strip(text: String): String = ChatColor.stripColor(color(text)) ?: ""
}