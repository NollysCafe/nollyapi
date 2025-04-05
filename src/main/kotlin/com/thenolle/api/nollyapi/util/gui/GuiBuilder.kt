package com.thenolle.api.nollyapi.util.gui

import com.thenolle.api.nollyapi.util.events.EventScope
import com.thenolle.api.nollyapi.util.events.fireEvent
import com.thenolle.api.nollyapi.util.gui.core.ButtonBuilder
import com.thenolle.api.nollyapi.util.gui.core.GuiButton
import com.thenolle.api.nollyapi.util.gui.events.GuiClickEvent
import com.thenolle.api.nollyapi.util.gui.events.GuiOpenEvent
import com.thenolle.api.nollyapi.util.gui.events.GuiRedirectEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A builder class for constructing and managing a GUI interface in a Bukkit-based Minecraft plugin.
 *
 * This class allows you to define a GUI with buttons, handle interactions, manage redirections between GUIs,
 * and perform various customizations to the GUI.
 */
class GuiBuilder private constructor(
    size: Int, private val title: String
) {
    private val inventory: Inventory = Bukkit.createInventory(null, size * 9, title)
    private val buttonMap: MutableMap<Int, GuiButton> = mutableMapOf()
    private val metadata: MutableMap<String, Any> = mutableMapOf()


    // Metadata
    /**
     * Retrieves the metadata associated with a key.
     *
     * @param key The key of the metadata.
     * @return The metadata value associated with the key, or null if not found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getMeta(key: String): T? = metadata[key] as? T

    /**
     * Sets metadata for the GUI.
     *
     * @param key The key of the metadata.
     * @param value The value of the metadata.
     * @return The builder instance for chaining.
     */
    fun <T> setMeta(key: String, value: T): GuiBuilder = apply { metadata[key] = value as Any }

    /**
     * Checks if the GUI has a specific metadata key.
     *
     * @param key The metadata key to check.
     * @return True if the metadata key exists, otherwise false.
     */
    fun hasMeta(key: String): Boolean = metadata.containsKey(key)

    /**
     * Removes a specific metadata key from the GUI.
     *
     * @param key The metadata key to remove.
     * @return The builder instance for chaining.
     */
    fun removeMeta(key: String): GuiBuilder = apply { metadata.remove(key) }

    /**
     * Retrieves all metadata keys associated with the GUI.
     *
     * @return A set of metadata keys.
     */
    fun metaKeys(): Set<String> = metadata.keys


    // Register Button (Full custom)
    /**
     * Registers a custom button in the GUI.
     *
     * @param slot The slot in the GUI where the button will be placed.
     * @param item The item to display on the button.
     * @param scope The event scope to trigger the button's action.
     * @param metadata Additional metadata associated with the button.
     * @param delay The delay before the button's action is triggered.
     * @param debounce The debounce time to prevent multiple clicks in quick succession.
     * @param throttle The throttle time to limit button action triggers.
     * @param onClick The action to be performed when the button is clicked.
     */
    fun button(
        slot: Int,
        item: ItemStack,
        scope: EventScope<InventoryClickEvent>.() -> Unit = {},
        metadata: Map<String, Any> = emptyMap(),
        delay: Long = 0L,
        debounce: Long = 0L,
        throttle: Long = 0L,
        onClick: (InventoryClickEvent) -> Unit
    ) {
        val button =
            GuiButton(item, onClick, EventScope.create(scope), metadata.toMutableMap(), delay, debounce, throttle)
        button.bind(inventory, slot)
        inventory.setItem(slot, item)
        buttonMap[slot] = button
        if (button.animationFrames.isNotEmpty()) button.startAnimation()
    }


    // Register Button (DSL)
    /**
     * Registers a button in the GUI using the [ButtonBuilder] DSL.
     *
     * @param slot The slot in the GUI where the button will be placed.
     * @param builder The builder function to configure the button's properties.
     */
    fun button(slot: Int, builder: ButtonBuilder.() -> Unit) {
        val built = ButtonBuilder().apply(builder).build()
        built.bind(inventory, slot)
        inventory.setItem(slot, built.item)
        buttonMap[slot] = built
        if (built.animationFrames.isNotEmpty()) built.startAnimation()
    }


    // Register Button (From template)
    /**
     * Registers a button in the GUI from a predefined [ButtonBuilder] template.
     *
     * @param slot The slot in the GUI where the button will be placed.
     * @param template The predefined template for the button.
     */
    fun button(slot: Int, template: ButtonBuilder) {
        val built = template.build()
        built.bind(inventory, slot)
        buttonMap[slot] = built
        inventory.setItem(slot, template.item ?: error("Template missing item"))
        if (built.animationFrames.isNotEmpty()) built.startAnimation()
    }


    // Register Button (Override template)
    /**
     * Registers a button in the GUI by overriding properties of an existing [ButtonBuilder] template.
     *
     * @param slot The slot in the GUI where the button will be placed.
     * @param template The predefined template for the button.
     * @param override The function to override specific properties of the button.
     */
    fun button(slot: Int, template: ButtonBuilder, override: ButtonBuilder.() -> Unit) {
        val copy = ButtonBuilder().apply {
            item = template.item
            onClick = template.onClick
            delay = template.delay
            debounce = template.debounce
            throttle = template.throttle
            metadata.putAll(template.metadata)
            scope = template.scope
        }.apply(override)

        val built = copy.build()
        built.bind(inventory, slot)
        inventory.setItem(slot, built.item)
        buttonMap[slot] = built
        if (built.animationFrames.isNotEmpty()) built.startAnimation()
    }


    // Open GUI
    /**
     * Opens the GUI for the specified player.
     *
     * @param player The player who will view the GUI.
     */
    fun open(player: Player) {
        GuiRegistry.register(inventory, this)
        player.openInventory(inventory)
        fireEvent(GuiOpenEvent(player, this))
    }


    // Get Inventory
    /**
     * Retrieves the [Inventory] representing the GUI.
     *
     * @return The inventory for this GUI.
     */
    fun getInventory() = inventory


    // Handle Button Click
    /**
     * Handles the click event on a button within the GUI.
     *
     * @param event The event triggered by the button click.
     */
    internal fun handleClick(event: InventoryClickEvent) {
        if (event.view.title != title) return
        event.isCancelled = true

        val button = buttonMap[event.slot] ?: return
        if (!button.scope.test(event)) return

        fireEvent(GuiClickEvent(event.whoClicked as Player, event.slot, event, button))
        button.trigger(event)
    }


    // Redirection
    /**
     * Redirects the player to a target GUI.
     *
     * @param player The player to redirect.
     * @param target The target GUI to redirect to.
     */
    fun redirectTo(player: Player, target: GuiBuilder) {
        fireEvent(GuiRedirectEvent(player, from = this, to = target))
        target.setMeta("from", this)
        target.setMeta("originalPage", this.getMeta<Int>("page") ?: 1)
        target.open(player)
    }

    /**
     * Redirects the player to a target GUI with additional metadata.
     *
     * @param player The player to redirect.
     * @param target The target GUI to redirect to.
     * @param meta Additional metadata to pass during the redirection.
     */
    fun redirectToWithMeta(player: Player, target: GuiBuilder, meta: Map<String, Any>) {
        meta.forEach { (key, value) -> target.setMeta(key, value) }
        redirectTo(player, target)
    }

    /**
     * Redirects the player to a target GUI and chains metadata between the two GUIs.
     *
     * @param player The player to redirect.
     * @param target The target GUI to redirect to.
     */
    fun chainRedirect(player: Player, target: GuiBuilder) {
        fireEvent(GuiRedirectEvent(player, from = this, to = target))
        this.metadata.forEach { (key, value) -> target.setMeta(key, value) }
        target.setMeta("from", this)
        target.open(player)
    }


    // Equipment Showcase
    /**
     * Populates the GUI with the player's equipment and hotbar items.
     *
     * @param player The player whose equipment and hotbar items will populate the GUI.
     */
    fun populateWithEquipment(player: Player) {
        val armor = player.inventory.armorContents
        val hotbar = player.inventory.contents.take(9)

        armor.forEachIndexed { index, item ->
            if (item != null) button(index) {
                this.item = item
                this.onClick = {}
            }
        }
        hotbar.forEachIndexed { index, item ->
            if (item != null) button(9 + index) {
                this.item = item
                this.onClick = {}
            }
        }
    }


    // Build & Templates
    companion object {
        /**
         * Builds a new [GuiBuilder] with the specified size, title, and configuration.
         *
         * @param size The number of rows in the GUI.
         * @param title The title of the GUI.
         * @param builder The function to configure the GUI.
         * @return The constructed [GuiBuilder] instance.
         */
        fun build(size: Int, title: String, builder: GuiBuilder.() -> Unit): GuiBuilder {
            val gui = GuiBuilder(size, title)
            gui.builder()
            return gui
        }

        /**
         * Retrieves a predefined button template by name.
         *
         * @param name The name of the template.
         * @return The [ButtonBuilder] template, or null if no template exists with the specified name.
         */
        fun getTemplate(name: String): ButtonBuilder? = GuiTemplate.templates[name]

        /**
         * Defines a new button template that can be reused.
         *
         * @param name The name of the template.
         * @param builder The function to configure the button in the template.
         */
        fun template(name: String, builder: ButtonBuilder.() -> Unit) =
            apply { GuiTemplate.templates[name] = ButtonBuilder().apply(builder) }
    }

    /**
     * A container for storing predefined button templates.
     */
    object GuiTemplate {
        val templates = mutableMapOf<String, ButtonBuilder>()
    }
}


// DSL entrypoint
/**
 * An entry point to create a new [GuiBuilder] using the builder pattern.
 *
 * @param size The number of rows in the GUI.
 * @param title The title of the GUI.
 * @param builder The configuration block to set up the GUI.
 * @return The constructed [GuiBuilder] instance.
 */
fun gui(size: Int, title: String, builder: GuiBuilder.() -> Unit): GuiBuilder = GuiBuilder.build(size, title, builder)