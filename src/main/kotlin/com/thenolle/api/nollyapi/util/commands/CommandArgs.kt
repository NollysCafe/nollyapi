package com.thenolle.api.nollyapi.util.commands

import com.thenolle.api.nollyapi.util.msg.MessageColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.reflect.KClass

/**
 * Extension function for [CommandContext] that ensures the sender is a player.
 *
 * If the sender is not a player, an error message is thrown.
 *
 * @return The [Player] instance representing the sender.
 * @throws IllegalArgumentException If the sender is not a player.
 */
fun CommandContext.requirePlayer(): Player =
    sender as? Player ?: errorMessage("This command can only be used by a player.")

/**
 * Extension function for [CommandContext] that retrieves and parses the argument at the specified index.
 *
 * The argument is parsed according to the provided [type]. If the argument is missing or cannot be parsed,
 * an error message is thrown.
 *
 * @param index The index of the argument to retrieve.
 * @param type The type of the argument to parse.
 * @return The parsed argument of type [T].
 * @throws IllegalArgumentException If the argument is missing or cannot be parsed.
 */
fun <T : Any> CommandContext.arg(index: Int, type: KClass<T>): T {
    val raw = args.getOrNull(index) ?: errorMessage("Missing argument #${index + 1}")
    return parseArg(raw, type) ?: errorMessage("Invalid argument type at #${index + 1}")
}

/**
 * Extension function for [CommandContext] that retrieves and parses the argument at the specified index,
 * or returns null if the argument is missing.
 *
 * @param index The index of the argument to retrieve.
 * @param type The type of the argument to parse.
 * @return The parsed argument of type [T] or null if the argument is missing.
 */
fun <T : Any> CommandContext.argOrNull(index: Int, type: KClass<T>): T? {
    val raw = args.getOrNull(index) ?: return null
    return parseArg(raw, type)
}

/**
 * Extension function for [CommandContext] that retrieves and parses the argument at the specified index,
 * or returns a default value if the argument is missing.
 *
 * @param index The index of the argument to retrieve.
 * @param type The type of the argument to parse.
 * @param default The default value to return if the argument is missing.
 * @return The parsed argument of type [T] or the default value.
 */
fun <T : Any> CommandContext.argOrDefault(index: Int, type: KClass<T>, default: T): T =
    argOrNull(index, type) ?: default

/**
 * Inline extension function for [CommandContext] that retrieves and parses the argument at the specified index.
 *
 * This function infers the type [T] from the reified type argument.
 *
 * @param index The index of the argument to retrieve.
 * @return The parsed argument of type [T].
 * @throws IllegalArgumentException If the argument is missing or cannot be parsed.
 */
inline fun <reified T : Any> CommandContext.arg(index: Int): T = arg(index, T::class)

/**
 * Inline extension function for [CommandContext] that retrieves and parses the argument at the specified index,
 * or returns null if the argument is missing.
 *
 * This function infers the type [T] from the reified type argument.
 *
 * @param index The index of the argument to retrieve.
 * @return The parsed argument of type [T] or null if the argument is missing.
 */
inline fun <reified T : Any> CommandContext.argOrNull(index: Int): T? = argOrNull(index, T::class)

/**
 * Inline extension function for [CommandContext] that retrieves and parses the argument at the specified index,
 * or returns a default value if the argument is missing.
 *
 * This function infers the type [T] from the reified type argument.
 *
 * @param index The index of the argument to retrieve.
 * @param default The default value to return if the argument is missing.
 * @return The parsed argument of type [T] or the default value.
 */
inline fun <reified T : Any> CommandContext.argOrDefault(index: Int, default: T): T =
    argOrDefault(index, T::class, default)

/**
 * Private function that parses a raw argument string into the specified type.
 *
 * Supported types include [String], [Int], [Double], [Boolean], [Player], and [Enum] types.
 *
 * @param value The raw string value of the argument.
 * @param type The type to parse the argument into.
 * @return The parsed value of type [T], or null if the argument cannot be parsed.
 */
@Suppress("UNCHECKED_CAST")
private fun <T : Any> parseArg(value: String, type: KClass<T>): T? {
    return when (type) {
        String::class -> value as T
        Int::class -> value.toIntOrNull() as? T
        Double::class -> value.toDoubleOrNull() as? T
        Boolean::class -> value.toBooleanStrictOrNull() as? T
        Player::class -> Bukkit.getPlayerExact(value) as? T
        else -> if (type.java.isEnum) {
            type.java.enumConstants.firstOrNull {
                (it as Enum<*>).name.equals(value, ignoreCase = true)
            } as? T
        } else null
    }
}

/**
 * Private function that throws an error message with the given [msg].
 *
 * @param msg The error message to display.
 * @throws IllegalArgumentException Always throws an [IllegalArgumentException].
 */
private fun errorMessage(msg: String): Nothing = throw IllegalArgumentException(MessageColor.color("&c$msg"))
