package com.thenolle.api.nollyapi.util.item

/**
 * Represents a definition for a custom item, including its unique ID and a builder function to create the item.
 *
 * @property id The unique identifier for the custom item.
 * @property builder A function that constructs the custom item.
 */
data class CustomItemDefinition (
    val id: String,
    val builder: () -> CustomItem
)