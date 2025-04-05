package com.thenolle.api.nollyapi.util.item

import com.thenolle.api.nollyapi.util.gui.GuiBuilder
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * Represents a custom item that can have event listeners associated with it.
 * These events can be triggered when the item is interacted with in various ways (e.g., left-click, right-click, drop, etc.).
 *
 * @param item The [ItemStack] that represents the custom item.
 * @param id The unique identifier for the custom item.
 */
class CustomItem(val item: ItemStack, val id: String) {
    // Sets the action to be performed when the item is left-clicked.
    fun onLeftClick(action: (Player) -> Unit): CustomItem = apply {
        onLeftClickAir = action
        onLeftClickBlock = action
    }
    // Sets the action to be performed when the item is left-clicked in the air.
    private var onLeftClickAir: ((Player) -> Unit)? = null
    fun onLeftClickAir(action: (Player) -> Unit): CustomItem = apply { onLeftClickAir = action }
    // Sets the action to be performed when the item is left-clicked on a block.
    private var onLeftClickBlock: ((Player) -> Unit)? = null
    fun onLeftClickBlock(action: (Player) -> Unit): CustomItem = apply { onLeftClickBlock = action }
    // Sets the action to be performed when the item is left-clicked while holding shift.
    fun onShiftLeftClick(action: (Player) -> Unit): CustomItem = apply {
        onShiftLeftClickAir = action
        onShiftLeftClickBlock = action
    }
    // Sets the action to be performed when the item is left-clicked in the air while holding shift.
    private var onShiftLeftClickAir: ((Player) -> Unit)? = null
    fun onShiftLeftClickAir(action: (Player) -> Unit): CustomItem = apply { onShiftLeftClickAir = action }
    // Sets the action to be performed when the item is left-clicked on a block while holding shift.
    private var onShiftLeftClickBlock: ((Player) -> Unit)? = null
    fun onShiftLeftClickBlock(action: (Player) -> Unit): CustomItem = apply { onShiftLeftClickBlock = action }

    // Sets the action to be performed when the item is right-clicked.
    fun onRightClick(action: (Player) -> Unit): CustomItem = apply {
        onRightClickAir = action
        onRightClickBlock = action
    }
    // Sets the action to be performed when the item is right-clicked in the air.
    private var onRightClickAir: ((Player) -> Unit)? = null
    fun onRightClickAir(action: (Player) -> Unit): CustomItem = apply { onRightClickAir = action }
    // Sets the action to be performed when the item is right-clicked on a block.
    private var onRightClickBlock: ((Player) -> Unit)? = null
    fun onRightClickBlock(action: (Player) -> Unit): CustomItem = apply { onRightClickBlock = action }
    // Sets the action to be performed when the item is right-clicked while holding shift.
    fun onShiftRightClick(action: (Player) -> Unit): CustomItem = apply {
        onShiftRightClickAir = action
        onShiftRightClickBlock = action
    }
    // Sets the action to be performed when the item is right-clicked in the air while holding shift.
    private var onShiftRightClickAir: ((Player) -> Unit)? = null
    fun onShiftRightClickAir(action: (Player) -> Unit): CustomItem = apply { onShiftRightClickAir = action }
    // Sets the action to be performed when the item is right-clicked on a block while holding shift.
    private var onShiftRightClickBlock: ((Player) -> Unit)? = null
    fun onShiftRightClickBlock(action: (Player) -> Unit): CustomItem = apply { onShiftRightClickBlock = action }

    // Sets the action to be performed when the item is dropped.
    private var onDrop: ((Player) -> Unit)? = null
    fun onDrop(action: (Player) -> Unit): CustomItem = apply { onDrop = action }

    // Sets the action to be performed when the item is picked up.
    private var onPickup: ((Player) -> Unit)? = null
    fun onPickup(action: (Player) -> Unit): CustomItem = apply { onPickup = action }

    // Sets the action to be performed when the item is interacted with a block.
    private var onBlockInteract: ((Player, Material) -> Unit)? = null
    fun onBlockInteract(action: (Player, Material) -> Unit): CustomItem = apply { onBlockInteract = action }

    // Sets the action to be performed when the item is interacted with an entity.
    private var onEntityInteract: ((Player, Entity) -> Unit)? = null
    fun onEntityInteract(action: (Player, Entity) -> Unit): CustomItem = apply { onEntityInteract = action }

    // Sets the action to be performed when the item breaks a block.
    private var onBlockBreak: ((Player, Material) -> Unit)? = null
    fun onBlockBreak(action: (Player, Material) -> Unit): CustomItem = apply { onBlockBreak = action }


    init {
        applyMetadata()
        CustomItemRegistry.register(id, this)
    }

    /**
     * Opens a GUI when the item is right-clicked.
     *
     * @param guiProvider A lambda function that takes a [Player] and returns a [GuiBuilder].
     *                    This function will be called when the item is right-clicked.
     */
    fun openGuiOnRightClick(guiProvider: (Player) -> GuiBuilder): CustomItem {
        return onRightClickAir { player -> guiProvider(player).open(player) }
    }

    /**
     * Opens a GUI when the item is right-clicked, passing the item as a parameter.
     *
     * @param guiProvider A lambda function that takes a [Player] and an [ItemStack], and returns a [GuiBuilder].
     *                    This function will be called when the item is right-clicked.
     */
    fun openGuiOnRightClickWithMeta(guiProvider: (Player, ItemStack) -> GuiBuilder): CustomItem {
        return onRightClickAir { player ->
            val item = player.inventory.itemInMainHand
            guiProvider(player, item).apply {
                setMeta("sourceItem", item)
                setMeta("customItem", this@CustomItem)
            }.open(player)
        }
    }

    /**
     * Gives the custom item to a player.
     *
     * @param player The [Player] to whom the item will be given.
     */
    fun giveTo(player: Player) = player.inventory.addItem(item)

    /**
     * Applies metadata to the item, including custom model data and lore.
     * This metadata is used to identify the item as a custom item.
     */
    private fun applyMetadata() {
        val meta = item.itemMeta ?: return
        try {
            meta.setCustomModelData(meta.customModelData + 1)
        } catch (_: Throwable) {
        }
        meta.lore = (meta.lore ?: emptyList()).plus("§8[custom:$id")
        item.itemMeta = meta
    }

    /**
     * Handles item interactions (e.g., left-click, right-click, etc.).
     *
     * @param player The player interacting with the item.
     * @param event The [PlayerInteractEvent] triggered by the interaction.
     */
    internal fun handleInteract(player: Player, event: PlayerInteractEvent) {
        val shift = player.isSneaking
        when (event.action) {
            Action.LEFT_CLICK_AIR -> {
                if (shift) onShiftLeftClickAir?.invoke(player)
                else onLeftClickAir?.invoke(player)
            }

            Action.LEFT_CLICK_BLOCK -> {
                if (shift) onShiftLeftClickBlock?.invoke(player)
                else onLeftClickBlock?.invoke(player)
            }

            Action.RIGHT_CLICK_AIR -> {
                if (shift) onShiftRightClickAir?.invoke(player)
                else onRightClickAir?.invoke(player)
            }

            Action.RIGHT_CLICK_BLOCK -> {
                if (shift) onShiftRightClickBlock?.invoke(player)
                else onRightClickBlock?.invoke(player)
            }

            else -> {}
        }
    }
}