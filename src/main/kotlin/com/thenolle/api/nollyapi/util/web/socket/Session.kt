package com.thenolle.api.nollyapi.util.web.socket

import io.ktor.server.plugins.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Represents a WebSocket session and provides methods for handling events and messages.
 *
 * This class allows WebSocket clients to interact with the server, send/receive messages, and join/leave rooms.
 */
class SocketSession(private val session: DefaultWebSocketServerSession) {
    val host: String get() = session.call.request.origin.remoteHost
    val id: UUID = UUID.randomUUID()

    var username: String? = null
    var player: Player? = null
    var room: String = "global"
    var closeReason: String? = null

    private val listeners = ConcurrentHashMap<String, suspend (JsonElement) -> Unit>()
    private var wildcardHandler: (suspend (String, JsonElement) -> Unit)? = null
    private val pendingAcknowledgments = mutableMapOf<String, suspend (JsonElement) -> Unit>()
    private var onCloseCallback: suspend (String) -> Unit = {}

    /**
     * Retrieves a header value from the WebSocket connection.
     *
     * @param key The header key to retrieve.
     * @return The header value, or null if not present.
     */
    fun header(key: String): String? = session.call.request.headers[key]

    /**
     * Joins a specific room for the session.
     *
     * @param newRoom The room name to join.
     * @return The updated [SocketSession] instance.
     */
    fun join(newRoom: String): SocketSession = apply {
        SocketRegistry.remove(this)
        room = newRoom
        SocketRegistry.add(this)
    }

    /**
     * Leaves the current room and reverts to the default "global" room.
     *
     * @return The updated [SocketSession] instance.
     */
    fun leave(): SocketSession = apply {
        SocketRegistry.remove(this)
        room = "global"
        SocketRegistry.add(this)
    }

    /**
     * Sends a message to the WebSocket client.
     *
     * @param text The message to send.
     * @return The updated [SocketSession] instance.
     */
    suspend fun send(text: String): SocketSession = apply {
        session.send(Frame.Text(text))
    }

    /**
     * Closes the WebSocket session with an optional reason.
     *
     * @param reason The reason for closing the connection (default is null).
     * @return The updated [SocketSession] instance.
     */
    suspend fun close(reason: String? = null): SocketSession = apply {
        closeReason = reason ?: "Unknown reason"
        onCloseCallback(closeReason ?: "Unknown reason")
        session.close()
    }

    /**
     * Sets the callback function to be invoked when the WebSocket session is closed.
     *
     * @param callback A suspend function to handle the close event.
     * @return The updated [SocketSession] instance.
     */
    fun onClose(callback: suspend (String) -> Unit): SocketSession = apply {
        onCloseCallback = callback
    }

    /**
     * Checks if the WebSocket session is still open.
     *
     * @return `true` if the session is open, otherwise `false`.
     */
    @Suppress("SENSELESS_COMPARISON")
    fun isOpen(): Boolean = session.closeReason == null

    /**
     * Emits an event with data to the WebSocket client, optionally requiring an acknowledgment.
     *
     * @param event The event name.
     * @param data The data to send with the event.
     * @param acknowledgment Whether an acknowledgment is required (default is false).
     * @param timeout The timeout for acknowledgment (default is 5000ms).
     * @return The updated [SocketSession] instance.
     */
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun emit(event: String, data: Any, acknowledgment: Boolean = false, timeout: Long = 5000L): SocketSession {
        val json = buildJsonObject {
            put("event", JsonPrimitive(event))
            put("data", wrap(data))
        }

        send(json.toString())

        if (acknowledgment) {
            val acknowledgmentListener: suspend (JsonElement) -> Unit = { response ->
                println("Acknowledgment received for event $event: $response")
            }
            pendingAcknowledgments[event] = acknowledgmentListener

            GlobalScope.launch {
                delay(timeout)
                if (pendingAcknowledgments.contains(event)) {
                    println("❌ Acknowledgment for event $event timed out.")
                    pendingAcknowledgments.remove(event)
                }
            }
        }

        return this
    }

    /**
     * Registers an event listener for a specific event.
     *
     * @param event The event name.
     * @param callback A suspend function to handle the event.
     * @return The updated [SocketSession] instance.
     */
    fun on(event: String, callback: suspend (JsonElement) -> Unit): SocketSession = apply {
        listeners[event] = callback
    }

    /**
     * Registers a typed event listener for a specific event, decoding the event data to the specified type.
     *
     * @param event The event name.
     * @param callback A suspend function to handle the event with a typed argument.
     * @return The updated [SocketSession] instance.
     */
    inline fun <reified T> onTyped(event: String, crossinline callback: suspend (T) -> Unit): SocketSession = apply {
        on(event) { raw ->
            try {
                val decoded = Json.decodeFromJsonElement<T>(raw)
                callback(decoded)
            } catch (exception: SerializationException) {
                println("❌ Failed to decode <$event>: ${exception.message}")
            }
        }
    }

    /**
     * Registers a wildcard event listener for all events.
     *
     * @param callback A suspend function to handle any event.
     * @return The updated [SocketSession] instance.
     */
    fun onAny(callback: suspend (String, JsonElement) -> Unit): SocketSession = apply {
        wildcardHandler = callback
    }

    /**
     * Handles raw incoming messages by parsing and invoking the appropriate event listeners.
     *
     * @param raw The raw message to handle.
     */
    internal suspend fun handleRaw(raw: String) {
        runCatching {
            val parsed = Json.parseToJsonElement(raw)
            val event = parsed.jsonObject["event"]?.jsonPrimitive?.content
            val data = parsed.jsonObject["data"]

            if (event != null && event.startsWith("ack_") && data != null) {
                val originalEvent = event.removePrefix("ack_")
                pendingAcknowledgments[originalEvent]?.invoke(data)
                pendingAcknowledgments.remove(originalEvent)
            } else {
                event?.let {
                    listeners[it]?.invoke(data ?: JsonNull)
                    wildcardHandler?.invoke(it, data ?: JsonNull)
                }
            }
        }.onFailure { exception ->
            println("❌ Failed to handle raw event. Message: $raw. Error: ${exception.message}")
        }
    }

    /**
     * Wraps any value into a [JsonElement].
     *
     * @param value The value to wrap into a [JsonElement].
     * @return The corresponding [JsonElement] representation of the value.
     */
    private fun wrap(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is JsonElement -> value
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Map<*, *> -> buildJsonObject {
            value.forEach { (key, v) ->
                if (key is String) put(key, wrap(v))
            }
        }

        is List<*> -> JsonArray(value.map { wrap(it) })
        else -> JsonPrimitive(value.toString())
    }
}