package com.thenolle.api.nollyapi.util.web.http

import com.thenolle.api.nollyapi.util.web.context.WebContext
import com.thenolle.api.nollyapi.util.web.middleware.WebMiddleware

/**
 * A builder for defining HTTP routes in the application, providing middleware support and a route handler.
 *
 * @property use A function that adds middleware to the route.
 * @property handle A function that defines how to handle the route requests.
 */
class WebRouteBuilder(
    val use: (WebMiddleware) -> Unit, val handle: (suspend WebContext.() -> Unit) -> Unit
) {
    /**
     * Adds middleware to the route.
     *
     * @param middleware The middleware to apply.
     */
    fun use(middleware: WebMiddleware) = use.invoke(middleware)

    /**
     * Adds middleware by name to the route.
     *
     * @param name The name of the middleware to apply.
     * @param middlewares The list of middleware to apply based on the name.
     */
    fun useNamed(name: String, middlewares: List<WebMiddleware>) =
        middlewares.filter { it.name == name }.forEach { use(it) }

    /**
     * Defines the handler function for the route.
     *
     * @param block The block that handles the request.
     */
    fun handle(block: suspend WebContext.() -> Unit) = handle.invoke(block)
}