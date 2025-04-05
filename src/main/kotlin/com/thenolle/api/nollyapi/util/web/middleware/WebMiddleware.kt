package com.thenolle.api.nollyapi.util.web.middleware

import io.ktor.server.application.*

/**
 * Defines a WebMiddleware interface used for handling HTTP requests in a Ktor application.
 * Middleware allows modifying the request/response pipeline or adding custom behavior for specific routes.
 *
 * Example:
 * ```kotlin
 * val loggingMiddleware = object : WebMiddleware {
 *     override suspend fun handle(call: ApplicationCall, next: suspend () -> Unit) {
 *         // Custom logic here
 *         next() // Call the next middleware or request handler
 *     }
 * }
 * ```
 */
fun interface WebMiddleware {
    /**
     * The function to handle the HTTP request. It can modify the request, perform actions,
     * or pass control to the next middleware/handler in the pipeline.
     *
     * @param call The [ApplicationCall] representing the HTTP request.
     * @param next The next function in the pipeline, called to continue the request processing.
     */
    suspend fun handle(call: ApplicationCall, next: suspend () -> Unit)

    /**
     * The name of the middleware. This is an optional property.
     * It can be used for logging, debugging, or middleware identification.
     */
    val name: String? get() = null
}