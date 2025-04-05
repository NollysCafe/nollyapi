package com.thenolle.api.nollyapi.util.msg

/**
 * Utility object for creating and building messages.
 *
 * This object provides a builder function that initializes a [MessageBuilder] to create complex messages
 * that can be sent to players or other command senders in the game.
 */
object Message {
    /**
     * Creates a new instance of [MessageBuilder] for constructing messages.
     *
     * @return A new instance of [MessageBuilder].
     */
    fun builder(): MessageBuilder = MessageBuilder()
}