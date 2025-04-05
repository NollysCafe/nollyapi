package com.thenolle.api.nollyapi.util.web.context

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.*

/**
 * Context class for handling HTTP requests in Ktor.
 *
 * This class provides utility methods to access parameters, headers, cookies, and other data from HTTP requests.
 * It also provides methods for responding to requests with JSON or text responses.
 *
 * @property call The [ApplicationCall] instance representing the HTTP request.
 */
class WebContext(val call: ApplicationCall) {
    /**
     * Retrieves a parameter from the request by key.
     *
     * @param key The parameter key.
     * @return The parameter value or null if not found.
     */
    fun param(key: String): String? = call.parameters[key] ?: call.request.queryParameters[key]

    /**
     * Retrieves a specific path segment by index.
     *
     * @param index The index of the path segment to retrieve.
     * @return The path segment at the specified index or null if not found.
     */
    fun path(index: Int): String? = call.request.path().split("/").getOrNull(index)

    /**
     * Retrieves a header from the request by key.
     *
     * @param key The header key.
     * @return The header value or null if not found.
     */
    fun header(key: String): String? = call.request.headers[key]

    /**
     * Retrieves a cookie value by key.
     *
     * @param key The cookie key.
     * @return The cookie value or null if not found.
     */
    fun cookie(key: String): String? = call.request.cookies[key]

    /**
     * Retrieves a cookie value by key with a default value if not found.
     *
     * @param key The cookie key.
     * @param defaultValue The default value to return if the cookie is not found.
     * @return The cookie value or the default value if not found.
     */
    fun cookie(key: String, defaultValue: String? = null): String? = call.request.cookies[key] ?: defaultValue

    /**
     * Retrieves all cookies as a map of key-value pairs.
     *
     * @return A map of all cookies in the request.
     */
    fun cookies(): Map<String, String> = call.request.cookies.rawCookies.toMap()

    /**
     * Checks if the HTTP method is GET.
     *
     * @return `true` if the method is GET, `false` otherwise.
     */
    fun isGet() = call.request.httpMethod == HttpMethod.Get

    /**
     * Checks if the HTTP method is POST.
     *
     * @return `true` if the method is POST, `false` otherwise.
     */
    fun isPost() = call.request.httpMethod == HttpMethod.Post

    /**
     * Checks if the HTTP method is PUT.
     *
     * @return `true` if the method is PUT, `false` otherwise.
     */
    fun isPut() = call.request.httpMethod == HttpMethod.Put

    /**
     * Checks if the HTTP method is DELETE.
     *
     * @return `true` if the method is DELETE, `false` otherwise.
     */
    fun isDelete() = call.request.httpMethod == HttpMethod.Delete

    /**
     * Checks if the HTTP method is PATCH.
     *
     * @return `true` if the method is PATCH, `false` otherwise.
     */
    fun isPatch() = call.request.httpMethod == HttpMethod.Patch

    /**
     * Checks if the HTTP method is HEAD.
     *
     * @return `true` if the method is HEAD, `false` otherwise.
     */
    fun isHead() = call.request.httpMethod == HttpMethod.Head

    /**
     * Checks if the HTTP method is OPTIONS.
     *
     * @return `true` if the method is OPTIONS, `false` otherwise.
     */
    fun isOptions() = call.request.httpMethod == HttpMethod.Options

    /**
     * Sends an "OK" response with a status code of 200.
     */
    suspend fun ok() = call.respondText("OK", ContentType.Text.Plain)

    /**
     * Sends a JSON response with the provided data.
     *
     * @param data The data to send in the response.
     * @param status The HTTP status code to return. Default is `200 OK`.
     */
    suspend fun json(data: Any?, status: HttpStatusCode = HttpStatusCode.OK) {
        val jsonElement = wrapDynamic(data)
        val response = buildJsonObject {
            put("success", JsonPrimitive(true))
            put("data", jsonElement)
        }
        call.respondText(Json.encodeToString(JsonObject.serializer(), response), ContentType.Application.Json, status)
    }

    /**
     * Sends an error message in JSON format.
     *
     * @param message The error message to send.
     * @param status The HTTP status code to return. Default is `400 Bad Request`.
     */
    suspend fun error(message: String, status: HttpStatusCode = HttpStatusCode.BadRequest) {
        val response = buildJsonObject {
            put("success", JsonPrimitive(false))
            put("error", JsonPrimitive(message))
        }
        call.respondText(Json.encodeToString(JsonObject.serializer(), response), ContentType.Application.Json, status)
    }

    /**
     * Sends the specified data as a JSON response with a specified status code.
     *
     * @param data The data to send in the response.
     * @param status The HTTP status code to return. Default is `200 OK`.
     */
    suspend inline fun <reified T : Any> send(data: T, status: HttpStatusCode = HttpStatusCode.OK) = json(data, status)

    /**
     * Parses the request body into an object of type [T].
     *
     * @return The parsed object.
     */
    suspend inline fun <reified T> body(): T {
        val text = call.receiveText()
        return Json.decodeFromString(text)
    }

    /**
     * Wraps dynamic data into a [JsonElement], converting various types into appropriate JSON types.
     *
     * @param data The data to wrap.
     * @return The wrapped [JsonElement].
     */
    private fun wrapDynamic(data: Any?): JsonElement = when (data) {
        null -> JsonNull
        is String -> JsonPrimitive(data)
        is Number -> JsonPrimitive(data)
        is Boolean -> JsonPrimitive(data)
        is Map<*, *> -> buildJsonObject {
            data.forEach { (key, value) -> if (key is String) put(key, wrapDynamic(value)) }
        }

        is List<*> -> JsonArray(data.map { wrapDynamic(it) })
        else -> JsonPrimitive(data.toString())
    }
}