package com.thenolle.api.nollyapi.util.web.http

import com.thenolle.api.nollyapi.util.web.context.WebContext
import io.ktor.http.*
import io.ktor.server.plugins.cors.CORSConfig
import io.ktor.server.plugins.cors.routing.CORS

/**
 * Extension function for adding a typed `POST` route to the `WebRouter`.
 * This route accepts a request body of type [T] and processes it using the provided handler.
 *
 * @param path The path for the route.
 * @param block The block that handles the request. The body of the request is automatically deserialized into type [T].
 */
internal inline fun <reified T> WebRouter.postTyped(
    path: String, crossinline block: suspend WebContext.(T) -> Unit
) {
    routes += {
        post(basePath + path) {
            handle {
                val body = body<T>()
                block(body)
            }
        }
    }
}

/**
 * Extension function for adding a typed `PUT` route to the `WebRouter`.
 * This route accepts a request body of type [T] and processes it using the provided handler.
 *
 * @param path The path for the route.
 * @param block The block that handles the request. The body of the request is automatically deserialized into type [T].
 */
internal inline fun <reified T> WebRouter.putTyped(
    path: String, crossinline block: suspend WebContext.(T) -> Unit
) {
    routes += {
        put(basePath + path) {
            handle {
                val body = body<T>()
                block(body)
            }
        }
    }
}

/**
 * Extension function for adding a typed `PATCH` route to the `WebRouter`.
 * This route accepts a request body of type [T] and processes it using the provided handler.
 *
 * @param path The path for the route.
 * @param block The block that handles the request. The body of the request is automatically deserialized into type [T].
 */
internal inline fun <reified T> WebRouter.patchTyped(
    path: String, crossinline block: suspend WebContext.(T) -> Unit
) {
    routes += {
        patch(basePath + path) {
            handle {
                val body = body<T>()
                block(body)
            }
        }
    }
}

/**
 * Extension function for adding a typed `DELETE` route to the `WebRouter`.
 * This route accepts a request body of type [T] and processes it using the provided handler.
 *
 * @param path The path for the route.
 * @param block The block that handles the request. The body of the request is automatically deserialized into type [T].
 */
internal inline fun <reified T> WebRouter.deleteTyped(
    path: String, crossinline block: suspend WebContext.(T) -> Unit
) {
    routes += {
        delete(basePath + path) {
            handle {
                val body = body<T>()
                block(body)
            }
        }
    }
}

/**
 * Extension function for adding a typed route with any HTTP method to the `WebRouter`.
 * The method is determined at runtime based on the [method] argument, and the request body is
 * automatically deserialized into type [T] for processing.
 *
 * @param method The HTTP method to handle (POST, PUT, PATCH, DELETE).
 * @param path The path for the route.
 * @param block The block that handles the request. The body of the request is automatically deserialized into type [T].
 */
internal inline fun <reified T> WebRouter.typed(
    method: HttpMethod, path: String, crossinline block: suspend WebContext.(T) -> Unit
) {
    when (method) {
        HttpMethod.Post -> postTyped(path, block)
        HttpMethod.Put -> putTyped(path, block)
        HttpMethod.Patch -> patchTyped(path, block)
        HttpMethod.Delete -> deleteTyped(path, block)
        else -> throw IllegalArgumentException("Unsupported method: $method")
    }
}

/**
 * Extension function to add CORS (Cross-Origin Resource Sharing) configuration to the `WebRouter`.
 * This function installs the CORS plugin and applies the provided configuration block.
 *
 * @param configure A block where CORS settings can be configured.
 */
internal fun WebRouter.cors(configure: CORSConfig.() -> Unit) {
    routes += {
        install(CORS) {
            configure()
        }
    }
}