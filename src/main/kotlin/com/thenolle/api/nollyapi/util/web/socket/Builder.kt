package com.thenolle.api.nollyapi.util.web.socket

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * A builder class used to configure WebSocket server sessions.
 *
 * This class allows for the setup of WebSocket event handlers such as connection, message reception, and disconnection.
 * It also provides middleware support to process events before passing to the handler.
 */
class SocketBuilder {
    private var onConnect: (suspend SocketSession.() -> Unit)? = null
    private var onMessage: (suspend SocketSession.(String) -> Unit)? = null
    private var onClose: (suspend (String) -> Unit)? = null

    private val middlewares = mutableListOf<suspend SocketSession.() -> Unit>()

    /**
     * Adds middleware to the WebSocket session.
     *
     * Middleware is applied before the session is opened.
     *
     * @param block A suspend function that can modify the session.
     * @return The [SocketBuilder] instance for chaining.
     */
    fun use(block: suspend SocketSession.() -> Unit) = apply { middlewares += block }

    /**
     * Sets the callback to be invoked when a WebSocket connection is established.
     *
     * @param block A suspend function that will be called upon connection.
     * @return The [SocketBuilder] instance for chaining.
     */
    fun onConnect(block: suspend SocketSession.() -> Unit) = apply { onConnect = block }

    /**
     * Sets the callback to be invoked when a message is received through the WebSocket connection.
     *
     * @param block A suspend function that will be called when a message is received.
     * @return The [SocketBuilder] instance for chaining.
     */
    fun onMessage(block: suspend SocketSession.(String) -> Unit) = apply { onMessage = block }

    /**
     * Sets the callback to be invoked when the WebSocket connection is closed.
     *
     * @param block A suspend function that will be called upon connection closure.
     * @return The [SocketBuilder] instance for chaining.
     */
    fun onClose(block: suspend (String) -> Unit) = apply { onClose = block }

    /**
     * Installs the WebSocket configuration for the given path.
     *
     * @param path The WebSocket endpoint path.
     * @param app The [Application] instance to which the WebSocket route will be added.
     */
    internal fun install(path: String, app: Application) {
        app.routing {
            webSocket(path) {
                val wrapped = SocketSession(this)
                SocketRegistry.add(wrapped)

                for (middleware in middlewares) {
                    middleware.invoke(wrapped)
                    if (!wrapped.isOpen()) {
                        SocketRegistry.remove(wrapped)
                        return@webSocket
                    }
                }

                onConnect?.invoke(wrapped)

                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            wrapped.handleRaw(message)
                            onMessage?.invoke(wrapped, message)

                            if (message.startsWith("ack_")) {
                                wrapped.emit(
                                    "ack_${message.removePrefix("ack_")}",
                                    JsonObject(mapOf("status" to JsonPrimitive("received"))),
                                    acknowledgment = true
                                )
                            }
                        }
                    }
                } finally {
                    val reason = wrapped.closeReason ?: "Disconnected"
                    onClose?.invoke(reason)
                    SocketRegistry.remove(wrapped)
                }
            }
        }
    }
}

/**
 * A function extension for installing WebSocket routes in an [Application].
 *
 * @param path The WebSocket route path.
 * @param builder A lambda to configure the [SocketBuilder].
 * @return The [Application] instance for chaining.
 */
fun Application.socket(path: String, builder: SocketBuilder.() -> Unit) = apply {
    SocketBuilder().apply(builder).install(path, this)
}