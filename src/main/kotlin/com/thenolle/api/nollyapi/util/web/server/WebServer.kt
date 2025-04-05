package com.thenolle.api.nollyapi.util.web.server

import com.thenolle.api.nollyapi.util.web.ApiResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import kotlin.time.Duration.Companion.minutes

/**
 * A utility object to manage a Ktor-based web server.
 * This provides methods to start and stop the web server with custom configurations and routes.
 *
 * The WebServer object encapsulates server setup, logging, exception handling, WebSockets, and more.
 */
object WebServer {
    private val logger = LoggerFactory.getLogger(WebServer::class.java)
    private var server: NettyApplicationEngine? = null
    private var running = false

    /**
     * Starts the web server with specified configurations.
     *
     * @param port The port on which the server will listen for incoming requests (default is 8080).
     * @param host The host address to bind the server to (default is "0.0.0.0" for all available interfaces).
     * @param routeBlock A block of code where you define routes for handling HTTP requests.
     */
    fun start(port: Int = 8080, host: String = "0.0.0.0", routeBlock: Application.() -> Unit) {
        if (running) return

        server = embeddedServer(Netty, port = port, host = host) {
            install(CallLogging) { level = Level.INFO }
            install(CallId) {
                generate { java.util.UUID.randomUUID().toString() }
                verify { it.isNotBlank() }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    val errorJson = Json.encodeToString(
                        ApiResponse<String>(false, error = cause.message ?: "Unknown error")
                    )
                    call.respondText(errorJson, ContentType.Application.Json, HttpStatusCode.InternalServerError)
                }
            }
            install(WebSockets) {
                pingPeriod = 1.minutes
                timeout = 2.minutes
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            routing { routeBlock() }
        }.start(wait = false).engine

        running = true
        logger.info("WebServer started at http://$host:$port/")
    }

    /**
     * Stops the web server if it is running.
     */
    fun stop() {
        if (!running) {
            logger.info("Server is not running.")
            return
        }

        server?.stop()
        running = false
        logger.info("WebServer stopped.")
    }
}

/**
 * Configures static file serving for a specified path and folder.
 *
 * @param path The URL path that will serve the files.
 * @param folder The folder in the server where the files are located.
 */
fun Application.staticFolder(path: String, folder: String) = apply {
    routing { staticFiles(path, File(folder)) }
}

/**
 * Configures static resources serving from a specified resource package.
 *
 * @param path The URL path that will serve the resources.
 * @param resourcePackage The package from which the resources will be served.
 */
fun Application.staticResources(path: String, resourcePackage: String) = apply {
    routing { staticResources(path, resourcePackage) }
}

/**
 * Configures static asset serving for a specified path with custom asset settings.
 *
 * @param path The URL path that will serve the assets.
 * @param configure A block of configuration for setting up static assets.
 */
fun Application.staticAssets(path: String, configure: StaticAssetBuilder.() -> Unit) = apply {
    routing { StaticAssetBuilder(this, path).apply(configure) }
}

/**
 * A builder class used for configuring static asset serving in the application.
 * This class provides methods to serve files from a folder or resources.
 */
class StaticAssetBuilder(private val route: Route, private val path: String) {
    /**
     * Serves files from a specified folder for a given path.
     *
     * @param directory The directory containing the files to serve.
     */
    fun folder(directory: String) = apply { route.staticFiles(path, File(directory)) }

    /**
     * Serves resources from a specified resource path for a given path.
     *
     * @param resourcePath The resource path from which the assets will be served.
     */
    fun resources(resourcePath: String) = apply { route.staticResources(path, resourcePath) }
}