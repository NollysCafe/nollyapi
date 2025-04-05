package com.thenolle.api.nollyapi.util.msg

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * DSL functions for creating and sending messages to one or more recipients.
 *
 * These extension functions allow for simplified message construction and sending using a builder pattern.
 * It supports sending messages to a single recipient, multiple recipients, or broadcasting to all players.
 */

// Single Recipient
/**
 * Sends a message to a single [CommandSender] using a [MessageBuilder].
 *
 * @param sender The target recipient of the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(sender: CommandSender, builder: MessageBuilder.() -> Unit) =
    Message.builder().to(sender).apply(builder)

/**
 * Sends a message to a single [Player] using a [MessageBuilder].
 *
 * @param player The target player of the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(player: Player, builder: MessageBuilder.() -> Unit) = Message.builder().to(player).apply(builder)

/**
 * Sends a message to a single recipient, which can be a [Player] or [CommandSender], using a [MessageBuilder].
 *
 * @param any The target recipient of the message (either a player or command sender).
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(any: Any, builder: MessageBuilder.() -> Unit) = when (any) {
    is Player -> message(any, builder)
    is CommandSender -> message(any, builder)
    else -> error("Unsupported target type: ${any::class.simpleName}")
}

/**
 * Sends a message with a custom prefix to a single [CommandSender] using a [MessageBuilder].
 *
 * @param sender The target recipient of the message.
 * @param prefix The custom prefix to be applied to the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(sender: CommandSender, prefix: String, builder: MessageBuilder.() -> Unit) =
    Message.builder().to(sender).apply {
        this.prefix = prefix
        builder()
    }

/**
 * Sends a message with a custom prefix to a single [Player] using a [MessageBuilder].
 *
 * @param player The target player of the message.
 * @param prefix The custom prefix to be applied to the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(player: Player, prefix: String, builder: MessageBuilder.() -> Unit) =
    Message.builder().to(player).apply {
        this.prefix = prefix
        builder()
    }

/**
 * Sends a message with a custom prefix to a single recipient, which can be a [Player] or [CommandSender], using a [MessageBuilder].
 *
 * @param any The target recipient of the message (either a player or command sender).
 * @param prefix The custom prefix to be applied to the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(any: Any, prefix: String, builder: MessageBuilder.() -> Unit) = when (any) {
    is Player -> message(any, prefix, builder)
    is CommandSender -> message(any, prefix, builder)
    else -> error("Unsupported target type: ${any::class.simpleName}")
}


// Multiple recipients
/**
 * Sends a message to multiple [Player]s using a [MessageBuilder].
 *
 * @param players The target players of the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(vararg players: Player, builder: MessageBuilder.() -> Unit) =
    players.forEach { Message.builder().to(it).apply(builder) }

/**
 * Sends a message to multiple [CommandSender]s using a [MessageBuilder].
 *
 * @param senders The target command senders of the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(vararg senders: CommandSender, builder: MessageBuilder.() -> Unit) =
    senders.forEach { Message.builder().to(it).apply(builder) }

/**
 * Sends a message to multiple [CommandSender]s from a collection using a [MessageBuilder].
 *
 * @param senders The collection of command senders to receive the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(senders: Collection<CommandSender>, builder: MessageBuilder.() -> Unit) =
    senders.forEach { Message.builder().to(it).apply(builder) }

/**
 * Sends a message to multiple recipients, which can be [Player]s or [CommandSender]s, using a [MessageBuilder].
 *
 * @param any The collection of target recipients (either players or command senders).
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun message(vararg any: Any, builder: MessageBuilder.() -> Unit) = any.forEach {
    when (it) {
        is Player -> message(it, builder)
        is CommandSender -> message(it, builder)
        else -> error("Unsupported target type: ${it::class.simpleName}")
    }
}


//Broadcast Helpers
/**
 * Broadcasts a message to all players using a [MessageBuilder].
 *
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun broadcast(builder: MessageBuilder.() -> Unit) = Message.builder().apply(builder)

/**
 * Broadcasts a message with a custom prefix to all players using a [MessageBuilder].
 *
 * @param prefix The custom prefix to be applied to the message.
 * @param builder The lambda that configures the [MessageBuilder].
 */
inline fun broadcast(prefix: String, builder: MessageBuilder.() -> Unit) = Message.builder().apply {
    this.prefix = prefix
    builder()
}