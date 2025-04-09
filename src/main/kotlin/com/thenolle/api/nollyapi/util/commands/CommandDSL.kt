package com.thenolle.api.nollyapi.util.commands

/**
 * A DSL function to create and register a command.
 *
 * This function simplifies the creation and registration of a command using a DSL-style builder.
 *
 * @param name The name of the command.
 * @param builder A lambda block used to configure the [CommandBuilder].
 */
fun command(name: String, builder: CommandBuilder.() -> Unit) {
    val built = CommandBuilder(name).apply(builder)
    CommandRegistrar.register(built)
}