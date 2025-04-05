package com.thenolle.api.nollyapi.util.web.socket

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * A registry for managing WebSocket sessions.
 *
 * This object maintains a collection of active WebSocket sessions and provides methods for sending messages,
 * broadcasting to all sessions, or targeting specific sessions by user ID or room.
 */
object SocketRegistry {
    private val sessions = mutableMapOf<String, MutableSet<SocketSession>>()

    /**
     * Adds a new WebSocket session to the registry.
     *
     * @param session The session to add to the registry.
     */
    fun add(session: SocketSession) {
        sessions.computeIfAbsent(session.room) { mutableSetOf() }.add(session)
    }

    /**
     * Removes a WebSocket session from the registry.
     *
     * @param session The session to remove from the registry.
     */
    fun remove(session: SocketSession) {
        sessions[session.room]?.remove(session)
    }

    /**
     * Emits an event to a specific session identified by the session ID.
     *
     * @param id The ID of the session.
     * @param event The event name.
     * @param data The event data to send.
     * @param acknowledgment Whether to request an acknowledgment for the event.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun emitToId(id: UUID, event: String, data: Any, acknowledgment: Boolean = false) {
        all().find { it.id == id }?.let {
            GlobalScope.launch { runCatching { it.emit(event, data, acknowledgment) } }
        }
    }

    /**
     * Emits an event to a session identified by the username.
     *
     * @param name The username of the session.
     * @param event The event name.
     * @param data The event data to send.
     * @param acknowledgment Whether to request an acknowledgment for the event.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun emitToUsername(name: String, event: String, data: Any, acknowledgment: Boolean = false) {
        all().find { it.username == name }?.let {
            GlobalScope.launch { runCatching { it.emit(event, data, acknowledgment) } }
        }
    }

    /**
     * Broadcasts a message to all active WebSocket sessions.
     *
     * @param message The message to broadcast to all sessions.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun broadcast(message: String) {
        all().forEach {
            GlobalScope.launch { runCatching { it.send(message) } }
        }
    }

    /**
     * Broadcasts a message to all sessions in a specific room.
     *
     * @param message The message to broadcast.
     * @param room The name of the room to broadcast to (default is "global").
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun broadcastRoom(message: String, room: String = "global") {
        sessions[room]?.forEach {
            GlobalScope.launch { runCatching { it.send(message) } }
        }
    }

    /**
     * Broadcasts a message to all sessions in a room, excluding the sender's session.
     *
     * @param sender The session to exclude from the broadcast.
     * @param message The message to broadcast.
     * @param room The name of the room (default is the sender's room).
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun broadcastExcept(sender: SocketSession, message: String, room: String? = sender.room) {
        sessions[room]?.filterNot { it.id == sender.id }?.forEach {
            GlobalScope.launch { runCatching { it.send(message) } }
        }
    }

    /**
     * Returns all active WebSocket sessions in the registry.
     *
     * @return A set of all active WebSocket sessions.
     */
    fun all(): Set<SocketSession> = sessions.values.flatten().toSet()
}