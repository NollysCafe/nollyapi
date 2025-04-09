package com.thenolle.api.nollyapi.util.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.command.ConsoleCommandSender

/**
 * Represents the context of a command execution.
 *
 * This class encapsulates the [sender] of the command, the [label] used to invoke the command, and the
 * [args] passed to the command. It is used to pass necessary information to command handlers and to
 * manage the execution flow.
 *
 * @property sender The sender of the command (typically a [Player] or [ConsoleCommandSender]).
 * @property label The label of the command used to invoke it.
 * @property args The arguments passed to the command.
 */
data class CommandContext(
    val sender: CommandSender,
    val label: String,
    val args: List<String>
)