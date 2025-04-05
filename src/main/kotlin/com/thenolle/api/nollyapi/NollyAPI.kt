package com.thenolle.api.nollyapi

import com.thenolle.api.nollyapi.util.events.EventListener
import com.thenolle.api.nollyapi.util.gui.GuiListener
import com.thenolle.api.nollyapi.util.item.CustomItemListener
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main class for the NollyAPI plugin.
 *
 * This class extends [JavaPlugin] and is responsible for initializing and managing the NollyAPI plugin.
 * It handles the plugin lifecycle, including the enabling and disabling of the plugin. Additionally,
 * it sets up event listeners and GUI listeners necessary for the plugin's operation.
 *
 * @constructor Initializes the plugin by configuring event listeners and other necessary components.
 */
class NollyAPI : JavaPlugin() {
    /**
     * Called when the plugin is enabled.
     *
     * This method is invoked by the Bukkit server when the plugin is first enabled. It performs tasks such as:
     * - Logging that the plugin has been enabled.
     * - Initializing necessary components like event listeners and GUI listeners.
     *
     * It is called only once during the plugin's lifecycle.
     */
    override fun onEnable() {
        // Initialize event listeners, GUI listeners and custom item listeners
        EventListener
        GuiListener
        CustomItemListener

        // Log that the plugin has been enabled
        logger.info("NollyAPI has been enabled!")
    }

    /**
     * Called when the plugin is disabled.
     *
     * This method is invoked by the Bukkit server when the plugin is unloaded or the server shuts down.
     * It typically handles tasks such as cleanup operations and logging.
     *
     * It is called only once during the plugin's lifecycle, just before the plugin is completely unloaded.
     */
    override fun onDisable() {
        // Log that the plugin has been disabled
        logger.info("NollyAPI has been disabled!")
    }
}