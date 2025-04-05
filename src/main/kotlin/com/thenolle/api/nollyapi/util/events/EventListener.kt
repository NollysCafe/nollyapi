package com.thenolle.api.nollyapi.util.events

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles event listening and registration for various events.
 *
 * This object provides methods to register event listeners with configurable options such as event priority,
 * debounce, throttle, async execution, and more. It also manages the unregistration of events.
 */
object EventListener {
    @PublishedApi
    internal var registeredPlugin: Plugin? = null

    /**
     * Initializes the event listener with the given [plugin].
     *
     * @param plugin The plugin to associate with the event listener.
     */
    fun init(plugin: Plugin) {
        this.registeredPlugin = plugin
    }

    /**
     * Registers a listener for the specified event type [T] with various configurable options.
     *
     * @param priority The priority at which the event is handled.
     * @param ignoreCancelled Whether or not the event is ignored if it is cancelled.
     * @param once If true, the listener will automatically unregister after it is triggered once.
     * @param delay The delay in milliseconds before the event is handled.
     * @param debounce The debounce time in milliseconds for event handling.
     * @param throttle The throttle time in milliseconds to limit the frequency of event handling.
     * @param async Whether the event should be handled asynchronously.
     * @param filter A filter function to determine if the event should be handled.
     * @param block The block of code to execute when the event is triggered.
     * @return An [EventHandle] that can be used to unregister the event listener.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Event> listen(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        once: Boolean = false,
        delay: Long = 0,
        debounce: Long = 0,
        throttle: Long = 0,
        async: Boolean = false,
        noinline filter: (T) -> Boolean = { true },
        noinline block: (T) -> Unit
    ): EventHandle {
        val clazz = T::class.java
        val plugin = registeredPlugin ?: error("EventListener.init(plugin) was not called!")

        lateinit var listener: Listener
        val throttleTracker = mutableMapOf<Class<*>, Long>()
        val debounceTracker = ConcurrentHashMap<Class<*>, BukkitRunnable>()

        val executor = EventExecutor { _, event ->
            if (!clazz.isInstance(event)) return@EventExecutor

            val casted = event as T
            if (!filter(casted)) return@EventExecutor

            val now = System.currentTimeMillis()

            if (throttle > 0) {
                val lastTime = throttleTracker[clazz] ?: 0
                if (now - lastTime < throttle) return@EventExecutor
                throttleTracker[clazz] = now
            }

            if (debounce > 0) {
                debounceTracker[clazz]?.cancel()
                debounceTracker[clazz] = object : BukkitRunnable() {
                    override fun run() {
                        block(casted)
                        if (once) HandlerList.unregisterAll(listener)
                    }
                }.apply { runTaskLater(plugin, debounce) }
                return@EventExecutor
            }

            if (delay > 0) {
                object : BukkitRunnable() {
                    override fun run() {
                        block(casted)
                        if (once) HandlerList.unregisterAll(listener)
                    }
                }.runTaskLater(plugin, delay)
                return@EventExecutor
            }

            if (async) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { block(casted) })
                if (once) HandlerList.unregisterAll(listener)
                return@EventExecutor
            }

            block(casted)
            if (once) HandlerList.unregisterAll(listener)
        }

        listener = object : Listener {}
        Bukkit.getPluginManager().registerEvent(clazz, listener, priority, executor, plugin, ignoreCancelled)

        return EventHandle {
            HandlerList.unregisterAll(listener)
            debounceTracker[clazz]?.cancel()
        }
    }

    /**
     * Registers a suspendable listener for the specified event type [T].
     *
     * This function is similar to [listen] but allows handling the event asynchronously using suspend functions.
     *
     * @param priority The priority at which the event is handled.
     * @param ignoreCancelled Whether or not the event is ignored if it is cancelled.
     * @param once If true, the listener will automatically unregister after it is triggered once.
     * @param delay The delay in milliseconds before the event is handled.
     * @param debounce The debounce time in milliseconds for event handling.
     * @param throttle The throttle time in milliseconds to limit the frequency of event handling.
     * @param filter A filter function to determine if the event should be handled.
     * @param block The suspend function to execute when the event is triggered.
     * @return An [EventHandle] that can be used to unregister the event listener.
     */
    @OptIn(DelicateCoroutinesApi::class)
    inline fun <reified T : Event> listenSuspend(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        once: Boolean = false,
        delay: Long = 0,
        debounce: Long = 0,
        throttle: Long = 0,
        noinline filter: (T) -> Boolean = { true },
        noinline block: suspend (T) -> Unit
    ): EventHandle {
        val clazz = T::class.java
        val plugin = registeredPlugin ?: error("EventListener.init(plugin) was not called!")

        lateinit var listener: Listener
        val throttleTracker = mutableMapOf<Class<*>, Long>()
        val debounceTracker = ConcurrentHashMap<Class<*>, BukkitRunnable>()

        val executor = EventExecutor { _, event ->
            if (!clazz.isInstance(event)) return@EventExecutor

            val casted = event as T
            if (!filter(casted)) return@EventExecutor

            val now = System.currentTimeMillis()

            if (throttle > 0) {
                val last = throttleTracker[clazz] ?: 0
                if (now - last < throttle) return@EventExecutor
                throttleTracker[clazz] = now
            }

            if (debounce > 0) {
                debounceTracker[clazz]?.cancel()
                debounceTracker[clazz] = object : BukkitRunnable() {
                    override fun run() {
                        GlobalScope.launch { block(casted) }
                        if (once) HandlerList.unregisterAll(listener)
                    }
                }.apply { runTaskLater(plugin, debounce) }
                return@EventExecutor
            }

            if (delay > 0) {
                object : BukkitRunnable() {
                    override fun run() {
                        GlobalScope.launch { block(casted) }
                        if (once) HandlerList.unregisterAll(listener)
                    }
                }.runTaskLater(plugin, delay)
                return@EventExecutor
            }

            GlobalScope.launch { block(casted) }
            if (once) HandlerList.unregisterAll(listener)
        }

        listener = object : Listener {}
        Bukkit.getPluginManager().registerEvent(clazz, listener, priority, executor, plugin, ignoreCancelled)

        return EventHandle {
            HandlerList.unregisterAll(listener)
            debounceTracker[clazz]?.cancel()
        }
    }
}

/**
 * Represents a handle for an event listener, allowing for unregistration.
 *
 * @param unregister The function to call to unregister the event listener.
 */
class EventHandle(private val unregister: () -> Unit) {
    fun unlisten() = unregister()
}