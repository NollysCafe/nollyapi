package com.thenolle.api.nollyapi.util.gui

import com.thenolle.api.nollyapi.util.gui.core.ButtonBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * A [PaginatedGuiBuilder] is used to create a paginated GUI, allowing players to navigate through multiple pages
 * of items. This builder provides functionality for setting up items, page navigation buttons, and customizing
 * the look of each page.
 *
 * @param baseTitle The base title of the paginated GUI.
 * @param rows The number of rows in the GUI.
 * @param data The list of data to be displayed in the GUI, which will be paginated.
 */
class PaginatedGuiBuilder<T> private constructor(
    private val baseTitle: String, private val rows: Int, private val data: List<T>
) {
    /** The slots available for buttons in the GUI, represented by a list of integers. */
    private var slots: List<Int> = (0 until (rows * 9)).toList()

    /** The number of items to display per page. */
    private var itemsPerPage: Int = slots.size

    /** The current page of the paginated GUI. */
    private var currentPage: Int = 1

    /** A function to render each item in the list of data on the GUI. */
    private var itemRenderer: (GuiBuilder, Int, T) -> Unit = { _, _, _ -> }

    /** A default item to fill the entire inventory if no items are set for specific slots. */
    private var fillAll: ItemStack? = null

    /** A map of custom items to fill specific slots in the inventory. */
    private var fillSlots: MutableMap<Int, ItemStack> = mutableMapOf()

    /** The slot where the page indicator will be displayed. */
    private var pageIndicatorSlot: Int? = null

    /** A custom title formatter for the GUI, which can be used to show page numbers. */
    private var customTitle: ((current: Int, max: Int) -> String)? = null

    /** The button for navigating to the previous page. */
    private var previousButton: ((gui: GuiBuilder, paginated: PaginatedGuiBuilder<T>) -> Unit)? = null

    /** The button for navigating to the next page. */
    private var nextButton: ((gui: GuiBuilder, paginated: PaginatedGuiBuilder<T>) -> Unit)? = null

    /**
     * Specifies the custom slots to be used in the GUI.
     *
     * @param customSlots The custom slot numbers to use for the items.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun slots(vararg customSlots: Int) = apply {
        slots = customSlots.toList()
        itemsPerPage = slots.size
    }

    /**
     * Sets a custom title formatter for the paginated GUI.
     *
     * @param formatter A function to format the title with the current page and total pages.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun titleFormatter(formatter: (current: Int, max: Int) -> String) = apply {
        customTitle = formatter
    }

    /**
     * Defines how to render each item in the list of data within the GUI.
     *
     * @param renderer A function that takes a [GuiBuilder], a slot index, and a data item to render.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun renderEach(renderer: (GuiBuilder, Int, T) -> Unit) = apply {
        itemRenderer = renderer
    }

    /**
     * Sets a default item to fill the entire inventory.
     *
     * @param item The [ItemStack] to fill all empty slots with.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun fillAll(item: ItemStack) = apply {
        fillAll = item
    }

    /**
     * Sets custom items for specific slots in the GUI.
     *
     * @param slot The slot to fill with the specified item.
     * @param item The [ItemStack] to place in the specified slot.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun fillSlots(vararg slot: Int, item: ItemStack) = apply {
        slot.forEach { fillSlots[it] = item }
    }

    /**
     * Sets a border around the inventory, filling the outermost slots with the specified item.
     *
     * @param item The [ItemStack] to use for the border.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun border(item: ItemStack) = apply {
        val totalSlots = rows * 9
        var borderSlots = mutableListOf<Int>()
        for (index in 0 until totalSlots) {
            val column = index % 9
            val row = index / 9
            if (column == 0 || column == 8 || row == 0 || row == rows - 1) {
                borderSlots += index
            }
        }
        borderSlots.forEach { fillSlots[it] = item }
    }

    /**
     * Specifies the slot for displaying the page indicator (e.g., "Page 1/5").
     *
     * @param slot The slot number to display the page indicator.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun pageIndicator(slot: Int) = apply {
        pageIndicatorSlot = slot
    }

    /**
     * Defines the button for navigating to the previous page.
     *
     * @param slot The slot number for the previous page button.
     * @param builder A function to configure the previous page button.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun previousButton(slot: Int, builder: ButtonBuilder.(PaginatedGuiBuilder<T>) -> Unit) = apply {
        previousButton = { gui, paginated ->
            if (currentPage <= 1) {
                gui.button(slot) {
                    item = ItemStack(Material.BARRIER).apply {
                        itemMeta = itemMeta?.apply { setDisplayName("§7No Previous Page") }
                    }
                    onClick = {}
                }
            } else {
                val button = ButtonBuilder().apply { builder(paginated) }
                gui.button(slot, button)
            }
        }
    }

    /**
     * Defines the button for navigating to the next page.
     *
     * @param slot The slot number for the next page button.
     * @param builder A function to configure the next page button.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun nextButton(slot: Int, builder: ButtonBuilder.(PaginatedGuiBuilder<T>) -> Unit) = apply {
        nextButton = { gui, paginated ->
            if (currentPage >= maxPage()) {
                gui.button(slot) {
                    item = ItemStack(Material.BARRIER).apply {
                        itemMeta = itemMeta?.apply { setDisplayName("§7No Next Page") }
                    }
                    onClick = {}
                }
            } else {
                val button = ButtonBuilder().apply { builder(paginated) }
                gui.button(slot, button)
            }
        }
    }

    /**
     * Decreases the current page by 1, if it is greater than 1.
     *
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun previousPage() = apply {
        if (currentPage > 1) {
            currentPage--
        }
    }

    /**
     * Sets the current page to a specific page number, ensuring it's within valid bounds.
     *
     * @param number The page number to set.
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun page(number: Int) = apply {
        this.currentPage = number.coerceIn(1, maxPage())
    }

    /**
     * Increases the current page by 1, if it's not the last page.
     *
     * @return The current [PaginatedGuiBuilder] instance for chaining.
     */
    fun nextPage() = apply {
        if (currentPage < maxPage()) {
            currentPage++
        }
    }

    /**
     * Calculates the maximum number of pages, based on the number of items and items per page.
     *
     * @return The maximum number of pages.
     */
    fun maxPage(): Int {
        if (itemsPerPage <= 0) return 1
        return (data.size + itemsPerPage - 1) / itemsPerPage
    }

    /**
     * Opens the paginated GUI for the specified player.
     *
     * @param player The player who will open the paginated GUI.
     */
    fun open(player: Player) {
        val finalTitle =
            customTitle?.invoke(currentPage, maxPage()) ?: baseTitle.replace("%page%", currentPage.toString())
                .replace("%max%", maxPage().toString())

        val gui = GuiBuilder.build(rows, finalTitle) {
            setMeta("page", currentPage)

            fillAll?.let { item ->
                (0 until (rows * 9)).forEach { slot ->
                    if (slot !in slots && slot !in fillSlots) {
                        button(slot) {
                            this.item = item
                            onClick = { }
                        }
                    }
                }
            }

            fillSlots.forEach { (slot, item) ->
                button(slot) {
                    this.item = item
                    onClick = {}
                }
            }

            val startIndex = (currentPage - 1) * itemsPerPage
            val endIndex = (startIndex + itemsPerPage).coerceAtMost(data.size)

            data.subList(startIndex, endIndex).forEachIndexed { index, item ->
                val slot = slots.getOrNull(index) ?: return@forEachIndexed
                itemRenderer(this, slot, item)
            }

            pageIndicatorSlot?.let { slot ->
                button(slot) {
                    item = ItemStack(Material.PAPER).apply {
                        itemMeta = itemMeta?.apply { setDisplayName("§bPage $currentPage / ${maxPage()}") }
                    }
                    onClick = {}
                }
            }

            previousButton?.invoke(this, this@PaginatedGuiBuilder)
            nextButton?.invoke(this, this@PaginatedGuiBuilder)
        }

        gui.open(player)
    }

    companion object {
        /**
         * Builds a new [PaginatedGuiBuilder] instance with the specified title, rows, and data list.
         *
         * @param title The title of the paginated GUI.
         * @param rows The number of rows in the GUI.
         * @param data The data to be displayed in the GUI.
         * @param builder A lambda block to configure the [PaginatedGuiBuilder] instance.
         * @return A new [PaginatedGuiBuilder] instance.
         */
        fun <T> build(
            title: String, rows: Int = 6, data: List<T>, builder: PaginatedGuiBuilder<T>.() -> Unit
        ): PaginatedGuiBuilder<T> {
            return PaginatedGuiBuilder(title, rows, data).apply(builder)
        }
    }
}