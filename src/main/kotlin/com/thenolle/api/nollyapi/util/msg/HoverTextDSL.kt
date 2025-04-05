package com.thenolle.api.nollyapi.util.msg

import net.md_5.bungee.api.chat.TextComponent

/**
 * DSL function to easily create a hoverable text component.
 *
 * @param text The initial text to display in the message.
 * @param builder The builder function to configure the text component.
 * @return The [TextComponent] with hover and click events applied.
 */
fun hoverable(text: String, builder: HoverTextBuilder.() -> Unit): TextComponent {
    return HoverTextBuilder(text).apply(builder).build()
}