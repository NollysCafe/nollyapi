package com.thenolle.api.nollyapi.util.events

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.java.JavaPlugin

/**
 * Registers a listener for the specified event type [T].
 *
 * This function allows listening to events with configurable parameters such as priority, cancellation behavior,
 * async execution, delay, throttle, debounce, and more.
 *
 * @param priority The priority at which the event is handled.
 * @param ignoreCancelled Whether or not the event is ignored if it is cancelled.
 * @param once If true, the listener will automatically unregister after it is triggered once.
 * @param delay The delay in milliseconds before the event is handled.
 * @param debounce The debounce time in milliseconds for event handling.
 * @param throttle The throttle time in milliseconds to limit the frequency of event handling.
 * @param async Whether the event should be handled asynchronously.
 * @param scope The scope of the event listener, where conditions for the event can be defined.
 * @param filter A filter function to determine if the event should be handled.
 * @param block The block of code to execute when the event is triggered.
 * @return An [EventHandle] that can be used to unregister the event listener.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <reified T : Event> listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    once: Boolean = false,
    delay: Long = 0,
    debounce: Long = 0,
    throttle: Long = 0,
    async: Boolean = false,
    crossinline scope: EventScope<T>.() -> Unit = {},
    noinline filter: (T) -> Boolean = { true },
    noinline block: (T) -> Unit
): EventHandle {
    val finalFilter: (T) -> Boolean = { EventScope.create(scope).test(it) && filter(it) }
    return EventListener.listen(priority, ignoreCancelled, once, delay, debounce, throttle, async, finalFilter, block)
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
 * @param scope The scope of the event listener, where conditions for the event can be defined.
 * @param filter A filter function to determine if the event should be handled.
 * @param block The suspend function to execute when the event is triggered.
 * @return An [EventHandle] that can be used to unregister the event listener.
 */
inline fun <reified T : Event> listenSuspend(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    once: Boolean = false,
    delay: Long = 0,
    debounce: Long = 0,
    throttle: Long = 0,
    crossinline scope: EventScope<T>.() -> Unit = {},
    noinline filter: (T) -> Boolean = { true },
    noinline block: suspend (T) -> Unit
): EventHandle {
    val finalFilter: (T) -> Boolean = { EventScope.create(scope).test(it) && filter(it) }
    return EventListener.listenSuspend(priority, ignoreCancelled, once, delay, debounce, throttle, finalFilter, block)
}

/**
 * Registers a listener for the specified event type [T] within the context of a [JavaPlugin].
 *
 * This function allows listening to events with configurable parameters specific to the plugin.
 *
 * @param priority The priority at which the event is handled.
 * @param ignoreCancelled Whether or not the event is ignored if it is cancelled.
 * @param once If true, the listener will automatically unregister after it is triggered once.
 * @param delay The delay in milliseconds before the event is handled.
 * @param debounce The debounce time in milliseconds for event handling.
 * @param throttle The throttle time in milliseconds to limit the frequency of event handling.
 * @param async Whether the event should be handled asynchronously.
 * @param scope The scope of the event listener, where conditions for the event can be defined.
 * @param filter A filter function to determine if the event should be handled.
 * @param block The block of code to execute when the event is triggered.
 * @return An [EventHandle] that can be used to unregister the event listener.
 */
inline fun <reified T : Event> JavaPlugin.on(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    once: Boolean = false,
    delay: Long = 0,
    debounce: Long = 0,
    throttle: Long = 0,
    async: Boolean = false,
    crossinline scope: EventScope<T>.() -> Unit = {},
    noinline filter: (T) -> Boolean = { true },
    noinline block: (T) -> Unit
): EventHandle {
    val finalFilter: (T) -> Boolean = { EventScope.create(scope).test(it) && filter(it) }
    return EventListener.listen(priority, ignoreCancelled, once, delay, debounce, throttle, async, finalFilter, block)
}

/**
 * Registers a listener for the specified event type [T] within the context of an [EventGroup].
 *
 * This function allows adding listeners to an event group for batch management.
 *
 * @param priority The priority at which the event is handled.
 * @param ignoreCancelled Whether or not the event is ignored if it is cancelled.
 * @param once If true, the listener will automatically unregister after it is triggered once.
 * @param delay The delay in milliseconds before the event is handled.
 * @param debounce The debounce time in milliseconds for event handling.
 * @param throttle The throttle time in milliseconds to limit the frequency of event handling.
 * @param async Whether the event should be handled asynchronously.
 * @param scope The scope of the event listener, where conditions for the event can be defined.
 * @param filter A filter function to determine if the event should be handled.
 * @param block The block of code to execute when the event is triggered.
 */
inline fun <reified T : Event> EventGroup.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    once: Boolean = false,
    delay: Long = 0,
    debounce: Long = 0,
    throttle: Long = 0,
    async: Boolean = false,
    crossinline scope: EventScope<T>.() -> Unit = {},
    noinline filter: (T) -> Boolean = { true },
    noinline block: (T) -> Unit
) {
    val finalFilter: (T) -> Boolean = { EventScope.create(scope).test(it) && filter(it) }
    val handle = EventListener.listen(priority, ignoreCancelled, once, delay, debounce, throttle, async, finalFilter, block)
    add(handle)
}