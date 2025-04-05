package com.thenolle.api.nollyapi.util.web.middleware

import io.ktor.server.application.*
import io.ktor.server.request.*

/**
 * A simple logging middleware that logs the HTTP method and URI of each incoming request.
 * It is an example of a custom middleware that can be used in a Ktor application.
 * This middleware logs the HTTP method and URI to the console and then proceeds to the next middleware/handler.
 */
object LoggingMiddleware : WebMiddleware {
    // The name of this middleware. It helps in identifying and debugging middlewares.
    override val name: String = "LoggingMiddleware"

    /**
     * The function to handle the HTTP request.
     * Logs the HTTP method and URI of the request and then proceeds with the request pipeline.
     *
     * @param call The [ApplicationCall] representing the HTTP request.
     * @param next The next function in the pipeline, called to continue the request processing.
     */
    override suspend fun handle(call: ApplicationCall, next: suspend () -> Unit) {
        // Log the HTTP method and URI
        val method = call.request.httpMethod.value
        val uri = call.request.uri
        println("$method $uri")

        // Proceed to the next handler/middleware
        next()
    }
}