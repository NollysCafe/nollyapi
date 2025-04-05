package com.thenolle.api.nollyapi.util.gui.core

import com.thenolle.api.nollyapi.util.events.EventScope
import com.thenolle.api.nollyapi.util.gui.GuiBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * A builder class for creating a [GuiButton] with various configuration options.
 *
 * This class allows you to define the behavior, appearance, and additional properties of a button in a GUI.
 * You can customize the button's item, click actions, animations, and other properties using this builder.
 */
class ButtonBuilder {
    /** The item that represents the button. */
    var item: ItemStack? = null

    /** The action to be performed when the button is clicked. */
    var onClick: ((InventoryClickEvent) -> Unit)? = null

    /** The metadata associated with the button. */
    val metadata: MutableMap<String, Any> = mutableMapOf()

    /** The delay (in ticks) before the button's action is triggered. */
    var delay: Long = 0L

    /** The debounce time (in ticks) to prevent multiple clicks in quick succession. */
    var debounce: Long = 0L

    /** The throttle time (in milliseconds) to limit how frequently the button action can be triggered. */
    var throttle: Long = 0L

    /** The event scope that defines the conditions for this button's action to be triggered. */
    var scope: EventScope<InventoryClickEvent>.() -> Unit = {}

    /** A list of item frames for animating the button. */
    var animationFrames: List<ItemStack> = emptyList()

    /** The interval (in ticks) between animation frames. */
    var animationInterval: Long = 20L

    /**
     * Adds frames for animating the button.
     *
     * @param frames The frames to use in the button's animation.
     * @return The builder instance for chaining.
     */
    fun frames(vararg frames: ItemStack) = apply { animationFrames = frames.toList() }

    /**
     * Sets the interval (in ticks) between animation frames.
     *
     * @param ticks The number of ticks between each animation frame.
     */
    fun interval(ticks: Long) = apply { animationInterval = ticks }

    /**
     * Builds the [GuiButton] with the defined properties.
     *
     * @return The constructed [GuiButton] instance.
     */
    fun build(): GuiButton {
        return GuiButton(
            item ?: animationFrames.firstOrNull() ?: error("Missing item in button"),
            onClick ?: error("Missing onClick action"),
            EventScope.create(scope),
            metadata,
            delay,
            debounce,
            throttle,
            animationFrames,
            animationInterval
        )
    }

    /**
     * Sets up a redirection from this button to another GUI builder when clicked.
     *
     * @param from The source GUI builder.
     * @param to The target GUI builder to redirect to.
     */
    fun redirectTo(from: GuiBuilder, to: GuiBuilder) {
        onClick = { event ->
            val player = event.whoClicked as? Player
            if (player != null) {
                to.setMeta("from", this)
                to.setMeta("originalPage", from.getMeta<Int>("page") ?: 1)
                from.redirectTo(player, to)
            }
        }
    }

    /**
     * Sets up a redirection with metadata from this button to another GUI builder when clicked.
     *
     * @param from The source GUI builder.
     * @param to The target GUI builder to redirect to.
     * @param meta Additional metadata to pass during the redirection.
     */
    fun redirectToWithMeta(from: GuiBuilder, to: GuiBuilder, meta: Map<String, Any>) {
        onClick = { event ->
            val player = event.whoClicked as? Player
            if (player != null) {
                from.redirectToWithMeta(player, to, meta)
            }
        }
    }

    /**
     * Sets up a chain redirection from this button to another GUI builder when clicked.
     *
     * @param from The source GUI builder.
     * @param to The target GUI builder to chain redirect to.
     */
    fun chainRedirect(from: GuiBuilder, to: GuiBuilder) {
        onClick = { event ->
            val player = event.whoClicked as? Player
            if (player != null) {
                from.chainRedirect(player, to)
            }
        }
    }
}
