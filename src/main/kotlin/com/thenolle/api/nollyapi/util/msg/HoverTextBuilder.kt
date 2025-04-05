package com.thenolle.api.nollyapi.util.msg

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Color

/**
 * A builder for creating custom hoverable text components in Minecraft.
 *
 * This class allows for creating text components with customizable hover text, click events, color, and formatting
 * for use in Minecraft chat. It supports a variety of click actions (run command, suggest command, open URL, etc.)
 * and also supports tooltips with multiple lines.
 *
 * @param text The initial text to display in the message.
 */
class HoverTextBuilder(text: String) {
    private val component = TextComponent(MessageColor.color(text))

    /**
     * Adds a tooltip that appears when hovering over the text.
     *
     * @param lines The lines of text to display in the tooltip.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun tooltip(vararg lines: String) = apply {
        val combined = lines.joinToString("\n") { MessageColor.color(it) }
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(combined))
    }

    /**
     * Sets a click event that runs a command when the text is clicked.
     *
     * @param command The command to run when the text is clicked.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun onClickCommand(command: String) = clickEvent(ClickEvent.Action.RUN_COMMAND, command)

    /**
     * Sets a click event that suggests a command when the text is clicked.
     *
     * @param command The command to suggest when the text is clicked.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun suggestCommand(command: String) = clickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)

    /**
     * Sets a click event that opens a URL when the text is clicked.
     *
     * @param url The URL to open when the text is clicked.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun openUrl(url: String) = clickEvent(ClickEvent.Action.OPEN_URL, url)

    /**
     * Sets a click event that copies text to the clipboard when the text is clicked.
     *
     * @param text The text to copy to the clipboard when clicked.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun copyToClipboard(text: String) = clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)

    /**
     * Helper method to set a click event with a specified action and value.
     *
     * @param action The click action to perform.
     * @param value The value associated with the click action.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    private fun clickEvent(action: ClickEvent.Action, value: String) =
        apply { component.clickEvent = ClickEvent(action, value) }

    /**
     * Sets the text that will be inserted when the text is clicked.
     *
     * @param text The text to insert when clicked.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun insertion(text: String) = apply { component.insertion = text }

    /**
     * Makes the text bold.
     *
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun bold() = apply { component.isBold = true }

    /**
     * Makes the text italic.
     *
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun italic() = apply { component.isItalic = true }

    /**
     * Underlines the text.
     *
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun underline() = apply { component.isUnderlined = true }

    /**
     * Strikes through the text.
     *
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun strikethrough() = apply { component.isStrikethrough = true }

    /**
     * Resets the text to its default formatting.
     *
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun reset() = apply { component.isReset = true }

    /**
     * Sets the color of the text.
     *
     * @param color The [ChatColor] to apply to the text.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun color(color: ChatColor) = apply { component.color = color }

    /**
     * Sets the color of the text using RGB values.
     *
     * @param r The red component (0-255).
     * @param g The green component (0-255).
     * @param b The blue component (0-255).
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun color(r: Int, g: Int, b: Int) = apply { component.color = ChatColor.of(Color.fromRGB(r, g, b).toString()) }

    /**
     * Appends additional text to the current text component.
     *
     * @param text The text to append.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun append(text: String) = apply { component.addExtra(TextComponent(MessageColor.color(text))) }

    /**
     * Appends a formatted text component with additional hover text and click events.
     *
     * @param text The text to append.
     * @param builder The builder function to configure the additional text component.
     * @return The [HoverTextBuilder] instance for method chaining.
     */
    fun appendFormatted(text: String, builder: HoverTextBuilder.() -> Unit) = apply {
        component.addExtra(HoverTextBuilder(text).apply(builder).build())
    }

    /**
     * Builds the final [TextComponent] with the specified properties.
     *
     * @return The final [TextComponent] with all added attributes.
     */
    fun build(): TextComponent = component
}