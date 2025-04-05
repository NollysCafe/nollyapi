package com.thenolle.api.nollyapi.util.web.http

import com.thenolle.api.nollyapi.util.web.context.WebContext
import com.thenolle.api.nollyapi.util.web.middleware.WebMiddleware
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * A router for managing the routing of HTTP requests with various methods (GET, POST, PUT, DELETE, etc.).
 *
 * @property basePath The base path for all routes defined within this router.
 */
class WebRouter(val basePath: String) {
    internal val routes = mutableListOf<Routing.() -> Unit>()
    val globalMiddlewares = mutableListOf<WebMiddleware>()
    private val routeMiddlewares = mutableMapOf<String, MutableList<WebMiddleware>>()

    /**
     * Defines a GET route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun get(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        WebRouteBuilder({ middlewares += it }, { handler = it }).apply(builder)

        routes += {
            get(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Defines a POST route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun post(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        routes += {
            post(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Defines a PUT route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun put(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        WebRouteBuilder({ middlewares += it }, { handler = it }).apply(builder)

        routes += {
            put(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Defines a DELETE route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun delete(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        WebRouteBuilder({ middlewares += it }, { handler = it }).apply(builder)

        routes += {
            delete(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Defines a PATCH route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun patch(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        WebRouteBuilder({ middlewares += it }, { handler = it }).apply(builder)

        routes += {
            patch(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Defines an OPTIONS route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun options(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        WebRouteBuilder({ middlewares += it }, { handler = it }).apply(builder)

        routes += {
            options(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Defines a HEAD route.
     *
     * @param path The path of the route.
     * @param builder The block to define the route's middleware and handler.
     */
    fun head(path: String, builder: WebRouteBuilder.() -> Unit) {
        val middlewares = mutableListOf<WebMiddleware>()
        var handler: (suspend WebContext.() -> Unit)? = null

        WebRouteBuilder({ middlewares += it }, { handler = it }).apply(builder)

        routes += {
            head(basePath + path) {
                applyMiddlewares(call, (globalMiddlewares + middlewares).iterator(), path) {
                    handler?.invoke(WebContext(call))
                }
            }
        }
    }

    /**
     * Adds middleware to be applied globally across all routes.
     *
     * @param middleware The middleware to add.
     */
    fun use(middleware: WebMiddleware) = globalMiddlewares.add(middleware)

    /**
     * Installs the router's routes and middlewares into a Ktor application.
     *
     * @param app The Ktor application to install the routes into.
     */
    fun installInto(app: Application) {
        app.routing {
            routes.forEach { it(this) }
        }
    }

    /**
     * Applies middleware in a sequential manner and invokes the final handler.
     *
     * @param call The application call.
     * @param iterator The iterator over the middlewares.
     * @param path The route path.
     * @param final The final handler to invoke after applying all middlewares.
     */
    suspend fun applyMiddlewares(
        call: ApplicationCall, iterator: Iterator<WebMiddleware>, path: String, final: suspend () -> Unit
    ) {
        if (iterator.hasNext()) {
            iterator.next().handle(call) { applyMiddlewares(call, iterator, path, final) }
        } else {
            final()
        }
    }
}