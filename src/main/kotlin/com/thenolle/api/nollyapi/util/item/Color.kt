package com.thenolle.api.nollyapi.util.item

import org.bukkit.ChatColor

/**
 * Extension function for [String] that adds color support using Minecraft's color codes.
 *
 * @return The string with applied color codes.
 */
fun String.color(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}