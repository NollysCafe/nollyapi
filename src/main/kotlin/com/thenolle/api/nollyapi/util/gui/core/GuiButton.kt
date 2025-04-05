package com.thenolle.api.nollyapi.util.gui.core

import com.thenolle.api.nollyapi.util.events.EventScope
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * Represents a clickable button within a GUI, which can have various actions, animations, and metadata.
 *
 * This class provides the ability to configure a button with an item, assign click actions, manage cooldowns,
 * set up animations, and store additional metadata.
 *
 * @param item The item that represents the button in the inventory.
 * @param onClick The action that will be triggered when the button is clicked.
 * @param scope The event scope that controls the conditions under which the button's action is triggered.
 * @param metadata The metadata associated with the button.
 * @param delay The delay (in ticks) before the button's action is triggered.
 * @param debounce The debounce time (in ticks) to prevent multiple clicks in quick succession.
 * @param throttle The throttle time (in milliseconds) to limit how frequently the button's action can be triggered.
 * @param animationFrames A list of item frames for animating the button.
 * @param animationInterval The interval (in ticks) between animation frames.
 */
class GuiButton(
    var item: ItemStack,
    val onClick: (InventoryClickEvent) -> Unit,
    val scope: EventScope<InventoryClickEvent> = EventScope(),
    val metadata: MutableMap<String, Any> = mutableMapOf(),
    val delay: Long = 0L,
    val debounce: Long = 0L,
    val throttle: Long = 0L,
    val animationFrames: List<ItemStack> = emptyList(),
    val animationInterval: Long = 20L
) {
    /** A map to track the debounce status for each player (by UUID). */
    private val debounceTracker = mutableMapOf<UUID, BukkitRunnable>()

    /** A map to track the throttle status for each player (by UUID). */
    private val throttleTracker = mutableMapOf<UUID, Long>()

    /** A task that controls the button's animation. */
    private var animationTask: BukkitRunnable? = null

    /** The index of the current frame in the animation. */
    private var currentFrame = 0

    /** A flag to indicate whether the animation is paused. */
    private var paused = false

    /** The inventory to which this button is bound. */
    private var parentInventory: Inventory? = null

    /** The slot in the inventory where this button is placed. */
    private var slot: Int = -1

    /**
     * Triggers the button's action when it is clicked, considering any debounce, delay, and throttle settings.
     *
     * @param event The event triggered by the button click.
     */
    fun trigger(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val uuid = player.uniqueId
        val now = System.currentTimeMillis()

        // Throttling: Prevent triggering the action too frequently
        if (throttle > 0) {
            val last = throttleTracker[uuid] ?: 0
            if (now - last < throttle) return
            throttleTracker[uuid] = now
        }

        // Debouncing: Delay the action to prevent multiple clicks in quick succession
        if (debounce > 0) {
            debounceTracker[uuid]?.cancel()
            debounceTracker[uuid] = object : BukkitRunnable() {
                override fun run() {
                    onClick(event)
                }
            }.apply { runTaskLater(Bukkit.getPluginManager().plugins.first(), debounce / 50) }
            return
        }

        // Delay: Delay the action before triggering
        if (delay > 0) {
            object : BukkitRunnable() {
                override fun run() {
                    onClick(event)
                }
            }.runTaskLater(Bukkit.getPluginManager().plugins.first(), delay / 50)
            return
        }

        // Trigger the action if no delay or debounce is applied
        onClick(event)
    }

    /**
     * Binds the button to a specific inventory and slot.
     *
     * @param inventory The inventory to bind the button to.
     * @param slot The slot in the inventory where the button should appear.
     */
    fun bind(inventory: Inventory, slot: Int) {
        this.parentInventory = inventory
        this.slot = slot
    }

    /**
     * Starts the animation for this button, cycling through the provided animation frames.
     */
    fun startAnimation() {
        // If no frames are defined or animation is already running, do nothing
        if (animationFrames.isEmpty() || animationTask != null) return

        // Create a task to run the animation
        animationTask = object : BukkitRunnable() {
            override fun run() {
                if (paused || animationFrames.isEmpty()) return

                // Set the current animation frame and update the item
                val frame = animationFrames[currentFrame]
                item = frame
                parentInventory?.setItem(slot, frame)

                // Move to the next frame, looping back to the first frame if necessary
                currentFrame = (currentFrame + 1) % animationFrames.size
            }
        }.apply {
            // Run the animation task at the specified interval
            runTaskTimer(Bukkit.getPluginManager().plugins.first(), animationInterval, animationInterval)
        }
    }

    /**
     * Stops the animation for this button, resetting the frame index.
     */
    fun stopAnimation() {
        animationTask?.cancel()
        animationTask = null
        currentFrame = 0
    }

    /**
     * Pauses the animation, stopping the frame updates.
     */
    fun pauseAnimation() = apply { paused = true }

    /**
     * Resumes the animation if it was paused.
     */
    fun resumeAnimation() = apply { paused = false }

    /**
     * Retrieves the metadata value associated with the specified key.
     *
     * @param key The key of the metadata.
     * @return The metadata value associated with the key, or null if not found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getMeta(key: String): T? = metadata[key] as? T

    /**
     * Sets the metadata value for a specific key.
     *
     * @param key The key for the metadata.
     * @param value The value to associate with the key.
     * @return The current [GuiButton] instance for method chaining.
     */
    fun <T> setMeta(key: String, value: T): GuiButton = apply { metadata[key] = value as Any }

    /**
     * Checks whether the button has metadata associated with the specified key.
     *
     * @param key The metadata key to check.
     * @return True if the metadata contains the key, otherwise false.
     */
    fun hasMeta(key: String): Boolean = metadata.containsKey(key)

    /**
     * Removes the metadata associated with the specified key.
     *
     * @param key The metadata key to remove.
     * @return The current [GuiButton] instance for method chaining.
     */
    fun removeMeta(key: String): GuiButton = apply { metadata.remove(key) }

    /**
     * Retrieves all the metadata keys associated with this button.
     *
     * @return A set of metadata keys.
     */
    fun metaKeys(): Set<String> = metadata.keys
}